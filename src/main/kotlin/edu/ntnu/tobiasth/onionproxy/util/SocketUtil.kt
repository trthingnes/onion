package edu.ntnu.tobiasth.onionproxy.util

import edu.ntnu.tobiasth.onionproxy.onion.OnionCircuit
import edu.ntnu.tobiasth.onionproxy.onion.stream.OnionInputStream
import edu.ntnu.tobiasth.onionproxy.onion.stream.OnionOutputStream
import mu.KotlinLogging
import java.io.*
import java.net.InetAddress
import java.net.Socket

/**
 * Utility class for socket connections.
 */
class SocketUtil {
    companion object {
        private val logger = KotlinLogging.logger {}

        /**
         * Gets a reader and writer from the given socket.
         * @param socket Socket to get IO for.
         * @return Reader and writer.
         */
        fun getSocketIO(socket: Socket): Pair<BufferedReader, PrintWriter> {
            return Pair(BufferedReader(InputStreamReader(getDirectInput(socket))), PrintWriter(getDirectOutput(socket)))
        }

        /**
         * Gets an input and output stream for a socket.
         * @param address IP-address of remote.
         * @param port Port of remote service.
         * @return Streams.
         */
        fun getSocketStreams(address: InetAddress, port: Int): Pair<InputStream, OutputStream> {
            return getSocketStreams(getSocket(address, port))
        }

        /**
         * Gets an input and output stream for a socket.
         * @param socket Socket to get streams for.
         * @return Streams.
         */
        fun getSocketStreams(socket: Socket): Pair<InputStream, OutputStream> {
            return Pair(getDirectInput(socket), getDirectOutput(socket))
        }

        /**
         * Gets an input and output stream for a socket to the given address.
         * This routes the connection through the circuit.
         * @param address IP-address of remote.
         * @param port Port of remote service.
         * @return Streams.
         * @see OnionCircuit
         */
        fun getOnionStreams(circuit: OnionCircuit, address: InetAddress, port: Int): Pair<InputStream, OutputStream> {
            circuit.begin(address, port)
            return Pair(getOnionInput(circuit), getOnionOutput(circuit))
        }

        /**
         * Creates a socket to the given address and port.
         * @param address IP-address of remote.
         * @param port Port of remote service.
         * @return Socket.
         */
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
