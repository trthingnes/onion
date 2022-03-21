package edu.ntnu.tobiasth.onionproxy.util

import edu.ntnu.tobiasth.onionproxy.Config
import edu.ntnu.tobiasth.onionproxy.onion.OnionCircuit
import edu.ntnu.tobiasth.onionproxy.onion.stream.OnionInputStream
import edu.ntnu.tobiasth.onionproxy.onion.stream.OnionOutputStream
import edu.ntnu.tobiasth.onionproxy.onion.OnionRouterDirectory
import mu.KotlinLogging
import java.io.*
import java.net.InetAddress
import java.net.Socket

class SocketUtil {
    companion object {
        private val logger = KotlinLogging.logger {}

        fun getSocketStreams(address: InetAddress, port: Int): Pair<InputStream, OutputStream> {
            logger.debug { "Creating socket to $address:$port." }
            val socket = Socket(address, port)
            logger.debug { "Socket ${if (socket.isConnected) "connected." else "not connected."}" }

            return getSocketStreams(socket)
        }

        fun getSocketStreams(socket: Socket): Pair<InputStream, OutputStream> {
            logger.debug { "Using ${if (Config.ONION_ENABLED) "onion" else "direct"} streams." }
            return if (Config.ONION_ENABLED) {
                val circuit = OnionUtil.createCircuit(Config.ONION_CIRCUIT_SIZE, OnionRouterDirectory())
                Pair(getOnionInput(socket, circuit), getOnionOutput(socket, circuit))
                TODO("Get router directory from somewhere.")
            } else {
                Pair(getDirectInput(socket), getDirectOutput(socket))
            }
        }

        private fun getDirectInput(socket: Socket) = BufferedInputStream(socket.getInputStream())
        private fun getDirectOutput(socket: Socket) = BufferedOutputStream(socket.getOutputStream())
        private fun getOnionInput(socket: Socket, circuit: OnionCircuit) = OnionInputStream(socket.getInputStream(), circuit)
        private fun getOnionOutput(socket: Socket, circuit: OnionCircuit) = OnionOutputStream(socket.getOutputStream(), circuit)
    }
}