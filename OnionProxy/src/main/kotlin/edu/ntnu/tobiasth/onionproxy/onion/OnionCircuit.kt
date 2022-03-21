package edu.ntnu.tobiasth.onionproxy.onion

import edu.ntnu.tobiasth.onionproxy.Config
import edu.ntnu.tobiasth.onionproxy.onion.cell.*
import edu.ntnu.tobiasth.onionproxy.util.DiffieHellmanUtil
import edu.ntnu.tobiasth.onionproxy.util.SocketUtil
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import javax.crypto.interfaces.DHPrivateKey

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
        send(pack(request))

        val response = unpack(receive())
        router.sharedSecret = getSharedSecret(response)
        routers.add(router)
    }

    /**
     * Destroys the connection to all servers in the circuit.
     */
    fun destroy() {
        TODO()
    }

    /**
     * Sends a cell to the first router in the circuit.
     */
    fun send(cell: OnionCell) {
        TODO()
    }

    /**
     * Blocks until a cell is received from the first router in the circuit.
     */
    fun receive(): OnionCell {
        TODO()
    }

    /**
     * Packs the given cell with one relay cell for each router it's going through.
     * If no routers have been connected yet, cell will be returned untouched.
     */
    private fun pack(cell: OnionCell): OnionCell {
        var packedCell: OnionRelayCell? = null

        for(r in routers.reversed()) {
            if (r.sharedSecret == null) {
                throw IllegalStateException("Tried to wrap onion cell, but shared secret was null")
            }
            packedCell = cell.wrap(r.sharedSecret!!, OnionRelayCommand.RELAY)
        }

        return packedCell ?: cell
    }

    /**
     * Unpacks the given cell by removing one relay cell for each router it has gone through.
     * If cell is not packed, it will be returned untouched.
     */
    private fun unpack(cell: OnionCell): OnionCell {
        var unpackedCell: OnionCell? = null

        for(r in routers.reversed()) {
            if (r.sharedSecret == null) {
                throw IllegalStateException("Tried to unwrap onion cell, but shared secret was null")
            }
            if (cell is OnionControlCell) {
                throw IllegalStateException("Cannot unwrap control cell")
            }
            unpackedCell = (cell as OnionRelayCell).unwrap(r.sharedSecret!!)
        }

        return unpackedCell ?: cell
    }

    /**
     * Extract a shared secret from the router response in create/extend.
     */
    private fun getSharedSecret(cell: OnionCell): ByteArray {
        if(cell.command != OnionControlCommand.CREATE) {
            throw IllegalStateException("Unexpected answer from server during create")
        }

        // ! Key exchange params are based on the client key.
        // ! The server will also have to use the params from the proxy public key.
        val routerKey = DiffieHellmanUtil.getPublicKeyFromEncoded(cell.data)
        val spec = (Config.ONION_PROXY_KEY.private as DHPrivateKey).params

        return DiffieHellmanUtil.getSharedSecret(Config.ONION_PROXY_KEY.private, routerKey, spec)
    }
}