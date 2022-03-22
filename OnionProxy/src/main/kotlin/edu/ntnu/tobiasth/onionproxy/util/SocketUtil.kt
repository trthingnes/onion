package edu.ntnu.tobiasth.onionproxy.util

import edu.ntnu.tobiasth.onionproxy.onion.OnionCircuit
import edu.ntnu.tobiasth.onionproxy.onion.stream.OnionInputStream
import edu.ntnu.tobiasth.onionproxy.onion.stream.OnionOutputStream
import mu.KotlinLogging
import java.io.*
import java.net.InetAddress
import java.net.Socket

class SocketUtil {
    companion object {
        private val logger = KotlinLogging.logger {}

        fun getSocketIO(address: InetAddress, port: Int): Pair<BufferedReader, PrintWriter> {
            return getSocketIO(getSocket(address, port))
        }

        fun getSocketIO(socket: Socket): Pair<BufferedReader, PrintWriter> {
            return Pair(BufferedReader(InputStreamReader(getDirectInput(socket))), PrintWriter(getDirectOutput(socket)))
        }

        fun getSocketStreams(address: InetAddress, port: Int): Pair<InputStream, OutputStream> {
            return getSocketStreams(getSocket(address, port))
        }

        fun getSocketStreams(socket: Socket): Pair<InputStream, OutputStream> {
            return Pair(getDirectInput(socket), getDirectOutput(socket))
        }

        fun getSocket(address: InetAddress, port: Int): Socket {
            logger.debug { "Creating socket to $address:$port." }
            return Socket(address, port)
        }

        private fun getDirectInput(socket: Socket) = BufferedInputStream(socket.getInputStream())
        private fun getDirectOutput(socket: Socket) = BufferedOutputStream(socket.getOutputStream())
        private fun getOnionInput(circuit: OnionCircuit) = OnionInputStream(circuit)
        private fun getOnionOutput(circuit: OnionCircuit) = OnionOutputStream(circuit)
    }
}