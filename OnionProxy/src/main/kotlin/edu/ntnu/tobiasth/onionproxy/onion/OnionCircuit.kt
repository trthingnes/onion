package edu.ntnu.tobiasth.onionproxy.onion

import edu.ntnu.tobiasth.onionproxy.Config
import edu.ntnu.tobiasth.onionproxy.onion.cell.*
import edu.ntnu.tobiasth.onionproxy.util.DiffieHellmanUtil
import edu.ntnu.tobiasth.onionproxy.util.OnionUtil
import edu.ntnu.tobiasth.onionproxy.util.SocketUtil
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import javax.crypto.interfaces.DHPublicKey

class OnionCircuit(val id: UUID, router: OnionRouterInfo) {
    private val routers = arrayListOf<OnionRouterInfo>()
    private val streams: Pair<InputStream, OutputStream>

    init {
        // Open a socket connection to first onion router.
        streams = SocketUtil.getSocketStreams(router.address, router.port)

        // Extend the network to the first router.
        extend(router)
    }

    /**
     * Extend the circuit to the router with the given info.
     */
    fun extend(router: OnionRouterInfo) {
        val request = OnionControlCell(id, OnionControlCommand.CREATE, Config.ONION_PROXY_KEY.public.encoded)
        send(addLayers(request))
        val response = removeLayers(receive()) as OnionControlCell

        router.sharedSecret = extractSharedSecret(response)
        routers.add(router)
    }

    /**
     * Destroys the connection to all routers in the circuit.
     */
    fun destroy() {
        TODO()
    }

    /**
     * Sends a cell to the first router in the circuit.
     */
    fun send(cell: OnionCell) {
        val data = OnionUtil.encryptCell(cell, routers.first().sharedSecret!!)
        streams.second.write(data)
        streams.second.flush()
    }

    /**
     * Blocks until a cell is received from the first router in the circuit.
     */
    fun receive(): OnionCell {
        // TODO: Not sure if this will work.
        val data = streams.first.readBytes()
        return OnionUtil.decryptCell(data, routers.first().sharedSecret!!)
    }

    /**
     * Packs the given cell with one relay cell for each router it's going through.
     * If no routers have been connected yet, cell will be returned untouched.
     */
    private fun addLayers(cell: OnionCell): OnionCell {
        var layeredCell: OnionCell = cell

        for(i in routers.lastIndex..1) {
            layeredCell = OnionRelayCell(
                layeredCell.circuitId,
                OnionUtil.encryptCell(layeredCell, routers[i].sharedSecret!!),
                OnionRelayCommand.RELAY
            )
        }

        return layeredCell
    }

    /**
     * Unpacks the given cell by removing one relay cell for each router it has gone through.
     * If cell is not packed, it will be returned untouched.
     */
    private fun removeLayers(layeredCell: OnionCell): OnionCell {
        var cell: OnionCell = layeredCell

        for(i in 1..routers.lastIndex) {
            cell = OnionUtil.decryptCell(cell.data, routers[i].sharedSecret!!)
        }

        return cell
    }

    /**
     * Extract a shared secret from the router response in create/extend.
     */
    private fun extractSharedSecret(cell: OnionControlCell): ByteArray {
        if(cell.command != OnionControlCommand.CREATE) {
            throw IllegalStateException("Unexpected answer from server during create")
        }

        // ! Key exchange params are based on the client key.
        // ! The server will also have to use the params from the proxy public key.
        val routerKey = DiffieHellmanUtil.getPublicKeyFromEncoded(cell.data)
        val spec = (Config.ONION_PROXY_KEY.public as DHPublicKey).params

        return DiffieHellmanUtil.getSharedSecret(Config.ONION_PROXY_KEY.private, routerKey, spec)
    }
}