package edu.ntnu.tobiasth.onionproxy.util

import edu.ntnu.tobiasth.onionproxy.Config
import mu.KotlinLogging
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.Socket

class SocketUtil {
    companion object {
        private val logger = KotlinLogging.logger {}

        fun getDirectInput(socket: Socket): DataInputStream = DataInputStream(socket.getInputStream())
        fun getDirectOutput(socket: Socket): BufferedOutputStream = BufferedOutputStream(socket.getOutputStream())
        //fun getOnionInput(socket: Socket, circuit: OnionCircuit): OnionInputStream = OnionInputStream(socket.getInputStream(), circuit: OnionCircuit)
        //fun getOnionOutput(socket: Socket, circuit: OnionCircuit): OnionOutputStream = OnionOutputStream(socket.getOutputStream(), circuit: OnionCircuit)

        fun getSocketStreams(address: InetAddress, port: Int): Pair<InputStream, OutputStream> {
            logger.debug { "Creating socket to $address:$port." }
            val socket = Socket(address, port)
            logger.debug { "Socket ${if (socket.isConnected) "connected." else "not connected."}" }

            logger.debug { "Using ${if (Config.ONION_ENABLED) "onion" else "direct"} streams." }
            if (Config.ONION_ENABLED) {
                TODO("Return onion streams")
            }
            else {
                return Pair(getDirectInput(socket), getDirectOutput(socket))
            }
        }
    }
}