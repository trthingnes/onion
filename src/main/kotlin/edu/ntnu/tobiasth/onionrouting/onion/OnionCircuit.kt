package edu.ntnu.tobiasth.onionrouting.onion

import edu.ntnu.tobiasth.onionrouting.Config
import edu.ntnu.tobiasth.onionrouting.onion.cell.*
import edu.ntnu.tobiasth.onionrouting.util.ByteArrayUtil
import edu.ntnu.tobiasth.onionrouting.util.DiffieHellmanUtil
import edu.ntnu.tobiasth.onionrouting.util.OnionUtil
import edu.ntnu.tobiasth.onionrouting.util.SocketUtil
import mu.KotlinLogging
import java.io.BufferedReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.Socket
import java.util.*
import javax.crypto.interfaces.DHPublicKey

/**
 * Chain of routers that cells can be sent through.
 * @see OnionRouterInfo
 * @see OnionCell
 */
class OnionCircuit(val id: UUID, router: OnionRouterInfo) {
    private val logger = KotlinLogging.logger {}
    private val routers = arrayListOf<OnionRouterInfo>()
    private val socket: Socket
    private val io: Pair<BufferedReader, PrintWriter>

    init {
        // Open a socket connection to first onion router.
        logger.debug { "Opening socket to first router in circuit." }
        socket = SocketUtil.getSocket(router.address, router.port)
        io = SocketUtil.getSocketIO(socket)

        create(router)
    }

    /**
     * Create a connection to the first router.
     * @param router Router to connect to.
     */
    private fun create(router: OnionRouterInfo) {
        val request = OnionControlCell(id, OnionControlCommand.CREATE, Config.ONION_PROXY_KEY.public.encoded)
        val response = exchange(request)

        if (response !is OnionControlCell) {
            throw IllegalStateException("Unexpected cell type")
        }

        if (response.command != OnionControlCommand.CREATE) {
            throw IllegalStateException("Expected cell command CREATE, got ${response.command}")
        }

        logger.debug { "Extracting shared secret." }
        router.sharedSecret = extractSharedSecret(response)
        routers.add(router)
    }

    /**
     * Extend the circuit to the router with the given info.
     * @param router Router to extend to.
     */
    fun extend(router: OnionRouterInfo) {
        if (routers.isEmpty()) {
            throw IllegalStateException("Cannot extend uninitialized circuit")
        }

        val routerId = Config.ONION_ROUTER_DIRECTORY.getId(router).toByte()
        val data = OnionUtil.serializeCell(
            OnionControlCell(id, OnionControlCommand.CREATE, Config.ONION_PROXY_KEY.public.encoded)
        )
        val request = OnionRelayCell(
            id,
            OnionRelayCommand.EXTEND,
            ByteArrayUtil.addByteToFront(data, routerId)
        )

        // Verify relay cell layer.
        val extendResponse = exchange(request)
        if (extendResponse !is OnionRelayCell) {
            throw IllegalStateException("Unexpected cell type ${extendResponse.javaClass}")
        }
        if (extendResponse.relayCommand != OnionRelayCommand.EXTEND) {
            throw IllegalStateException("Expected command EXTEND, but got ${extendResponse.command}")
        }

        // Verify control cell.
        val createResponse = OnionUtil.deserializeCell(extendResponse.data)
        if (createResponse !is OnionControlCell) {
            throw IllegalStateException("Unexpected cell type ${extendResponse.javaClass}")
        }
        if (createResponse.command != OnionControlCommand.CREATE) {
            throw IllegalStateException("Expected command CREATE, but got ${extendResponse.command}")
        }

        router.sharedSecret = extractSharedSecret(createResponse)
        routers.add(router)
    }

    /**
     * Truncate the circuit and close the connection to the last router.
     */
    fun truncate() {
        val request = OnionControlCell(id, OnionControlCommand.DESTROY, ByteArray(0))
        send(request)
        val response = receive() as OnionControlCell

        if (response.command == OnionControlCommand.DESTROY) {
            routers.removeLast()

            if (routers.isEmpty()) {
                // If we removed the last router, we are the ones who have to close the socket.
                socket.close()
            }
        } else {
            throw IllegalStateException("Unexpected response from router during truncate")
        }
    }

    /**
     * Destroys the connection to all routers in the circuit.
     */
    fun destroy() {
        repeat(routers.size) { truncate() }
    }

    /**
     * Establishes an external connection through the circuit.
     * @param address IP-address of the external server.
     * @param port Port of service on external server.
     */
    fun begin(address: InetAddress, port: Int) {
        val request = OnionRelayCell(id, OnionRelayCommand.BEGIN, SocketInfo(address, port).serialize())
        val response = exchange(request)

        if (response !is OnionRelayCell) {
            throw IllegalStateException("Unexpected cell type ${response.javaClass}")
        }
        if (response.relayCommand != OnionRelayCommand.BEGIN) {
            throw IllegalStateException("Expected command BEGIN, but got ${response.command}")
        }
    }

    /**
     * Ends the running external connection.
     */
    fun end() {
        val request = OnionRelayCell(id, OnionRelayCommand.END, ByteArray(0))
        val response = exchange(request)

        if (response !is OnionRelayCell) {
            throw IllegalStateException("Unexpected cell type ${response.javaClass}")
        }
        if (response.relayCommand != OnionRelayCommand.END) {
            throw IllegalStateException("Expected command END, but got ${response.command}")
        }
    }

    /**
     * Adds encryption layers and sends a cell to the first router in the circuit.
     * @param cell Cell to send to the last router.
     */
    fun send(cell: OnionCell) {
        val data = if (routers.isNotEmpty()) {
            OnionUtil.encryptCell(addRelayLayers(cell), routers.first().sharedSecret!!)
        } else {
            logger.trace { "Serializing outgoing cell." }
            OnionUtil.serializeCell(cell)
        }

        io.second.println(Base64.getEncoder().encodeToString(data))
        io.second.flush()
    }

    /**
     * Blocks until a cell is received from the last router and removes encryption layers.
     * @return Cell received from the last router.
     */
    fun receive(): OnionCell {
        val data = Base64.getDecoder().decode(io.first.readLine()) ?: ByteArray(0)

        val cell = if (routers.isNotEmpty()) {
            removeRelayLayers(OnionUtil.decryptCell(data, routers.first().sharedSecret!!))
        } else {
            logger.trace { "Deserializing incoming cell." }
            OnionUtil.deserializeCell(data)
        }

        return cell
    }

    /**
     * Convenience method combining send and receive.
     * @param cell Cell to send to the last router.
     * @return Cell received from the last router.
     */
    fun exchange(cell: OnionCell): OnionCell {
        logger.debug { "Sending request." }
        send(cell)

        logger.debug { "Waiting for response." }
        val response = receive()

        logger.debug { "Received response." }
        return response
    }

    /**
     * Packs the given cell with one relay cell for each router it's going through.
     * If no routers have been connected yet, cell will be returned untouched.
     */
    private fun addRelayLayers(cell: OnionCell): OnionCell {
        var layeredCell: OnionCell = cell

        if (routers.isNotEmpty()) {
            for (i in (1..routers.lastIndex).reversed()) {
                logger.trace { "Adding a relay layer." }
                layeredCell = OnionRelayCell(
                    layeredCell.circuitId,
                    OnionRelayCommand.RELAY,
                    OnionUtil.encryptCell(layeredCell, routers[i].sharedSecret!!)
                )
            }
        }

        return layeredCell
    }

    /**
     * Unpacks the given cell by removing one relay cell for each router it has gone through.
     * If cell is not packed, it will be returned untouched.
     */
    private fun removeRelayLayers(layeredCell: OnionCell): OnionCell {
        var cell: OnionCell = layeredCell

        if (routers.isNotEmpty()) {
            for (i in 1..routers.lastIndex) {
                logger.trace { "Removing a relay layer." }
                cell = OnionUtil.decryptCell(cell.data, routers[i].sharedSecret!!)
            }
        }

        return cell
    }

    /**
     * Extract a shared secret from the router response in create/extend.
     */
    private fun extractSharedSecret(cell: OnionControlCell): ByteArray {
        if (cell.command != OnionControlCommand.CREATE) {
            throw IllegalStateException("Unexpected answer from server during create")
        }

        // ! Key exchange params are based on the client key.
        // ! The server will also have to use the params from the proxy public key.
        val routerKey = DiffieHellmanUtil.getPublicKeyFromEncoded(cell.data)
        val spec = (Config.ONION_PROXY_KEY.public as DHPublicKey).params

        return DiffieHellmanUtil.getSharedSecret(Config.ONION_PROXY_KEY.private, routerKey, spec)
    }
}
