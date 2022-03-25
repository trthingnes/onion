package edu.ntnu.tobiasth.onionrouting.onion

import edu.ntnu.tobiasth.onionrouting.Config
import edu.ntnu.tobiasth.onionrouting.onion.cell.OnionControlCell
import edu.ntnu.tobiasth.onionrouting.onion.cell.OnionControlCommand
import edu.ntnu.tobiasth.onionrouting.onion.cell.OnionRelayCell
import edu.ntnu.tobiasth.onionrouting.onion.cell.OnionRelayCommand
import edu.ntnu.tobiasth.onionrouting.util.ByteArrayUtil
import edu.ntnu.tobiasth.onionrouting.util.DiffieHellmanUtil
import edu.ntnu.tobiasth.onionrouting.util.OnionUtil
import edu.ntnu.tobiasth.onionrouting.util.SocketUtil
import mu.KotlinLogging
import java.io.BufferedReader
import java.io.InputStream
import java.io.OutputStream
import java.io.PrintWriter
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.security.KeyPair
import java.util.*
import kotlin.concurrent.thread
import kotlin.system.exitProcess

// * To test the router circuit creation, run this.
fun main() {
    val ports = mutableListOf<Int>()
    ports.addAll(Config.ONION_ROUTER_PORTS)

    ports.forEach {
        thread { OnionRouter(it) }
    }

    println("Waiting to allow routers to start.")
    Thread.sleep(3000)

    println("Creating circuit.")
    val c = OnionCircuit(UUID.randomUUID(), OnionRouterInfo(InetAddress.getLoopbackAddress(), ports.removeFirst()))

    for (port in ports) {
        println("Extending circuit to $port.")
        c.extend(OnionRouterInfo(InetAddress.getLoopbackAddress(), port))
    }

    println("Success!")
    exitProcess(0)
}

/**
 * Router that can be a part of a circuit.
 * @see OnionCircuit
 */
class OnionRouter(port: Int) {
    private val logger = KotlinLogging.logger {}
    private val keypair = DiffieHellmanUtil.getKeyPair()

    init {
        val server = ServerSocket(port)
        logger.info { "Waiting for connections on port $port." }

        while (true) {
            val socket = server.accept()
            thread {
                try {
                    logger.info { "New connection from client at ${socket.inetAddress}:${socket.port}." }
                    ClientHandler(socket, keypair)
                } catch (e: Exception) {
                    logger.error { "An error occured with the message '${e.message}'." }
                    logger.debug { "Closing socket." }
                    socket.close()
                }
            }
        }
    }

    /**
     * Subclass to keep track of connection information for each circuit.
     */
    class ClientHandler(clientSocket: Socket, private val keypair: KeyPair) {
        private val logger = KotlinLogging.logger {}
        private val clientIo: Pair<BufferedReader, PrintWriter> = SocketUtil.getSocketIO(clientSocket)
        private var targetIsRouter: Boolean = true
        private var targetSocket: Socket? = null
        private lateinit var targetIo: Pair<BufferedReader, PrintWriter>
        private lateinit var externalIo: Pair<InputStream, OutputStream>
        private lateinit var sharedSecret: ByteArray

        init {
            handleHandshake()

            while (!clientSocket.isClosed) {
                handleRequest()
            }
        }

        private fun handleHandshake() {
            val handshake = OnionUtil.deserializeCell(readByteArray(clientIo.first))
            logger.debug { "Received handshake with id ${handshake.circuitId}." }

            if (handshake.command != OnionControlCommand.CREATE) {
                throw IllegalStateException("Expected command CREATE, but got ${handshake.command}")
            }

            val publicKey = DiffieHellmanUtil.getPublicKeyFromEncoded(handshake.data)
            sharedSecret = DiffieHellmanUtil.getSharedSecret(keypair.private, publicKey, publicKey.params)
            logger.debug { "Established shared secret." }

            val response = OnionControlCell(handshake.circuitId, OnionControlCommand.CREATE, keypair.public.encoded)
            logger.debug { "Writing handshake response." }
            writeByteArray(clientIo.second, OnionUtil.serializeCell(response))
        }

        /**
         * Handles request from proxy or router.
         * @see OnionProxy
         * @see OnionRouter
         */
        private fun handleRequest() {
            logger.trace { "Waiting to read cell." }
            val data = readByteArray(clientIo.first)

            val response = when (val request = OnionUtil.decryptCell(data, sharedSecret)) {
                is OnionControlCell -> {
                    logger.debug { "Received a control cell with command ${request.command}." }
                    handleControlCell(request)
                }
                is OnionRelayCell -> {
                    logger.debug { "Received a relay cell with command ${request.relayCommand}." }
                    handleRelayCell(request)
                }
                else -> {
                    throw IllegalStateException("Unexpected cell type ${request.javaClass}")
                }
            }

            writeByteArray(clientIo.second, OnionUtil.encryptCell(response, sharedSecret))
        }

        /**
         * Handles control cell from proxy.
         * @see OnionProxy
         * @see OnionControlCell
         * @see OnionControlCommand
         */
        private fun handleControlCell(cell: OnionControlCell): OnionControlCell {
            when (cell.command) {
                OnionControlCommand.DESTROY -> {
                    return OnionControlCell(cell.circuitId, OnionControlCommand.DESTROY, ByteArray(0))
                }
                else -> {
                    throw IllegalStateException("Unexpected command ${cell.command}")
                }
            }
        }

        /**
         * Handles relay cell from proxy.
         * @see OnionProxy
         * @see OnionRelayCell
         * @see OnionRelayCommand
         */
        private fun handleRelayCell(cell: OnionRelayCell): OnionRelayCell {
            when (cell.relayCommand) {
                OnionRelayCommand.RELAY -> {
                    writeByteArray(targetIo.second, cell.data)
                    return OnionRelayCell(cell.circuitId, OnionRelayCommand.RELAY, readByteArray(targetIo.first))
                }

                // This method uses external io, not target io.
                OnionRelayCommand.DATA -> {
                    if (targetSocket == null || targetIsRouter) {
                        throw IllegalStateException("There is no remote connection to send data to")
                    }

                    logger.trace { "Writing data to external connection:" }
                    logger.trace { String(cell.data) }
                    externalIo.second.write(cell.data)
                    externalIo.second.flush()

                    logger.trace { "Waiting for data to be returned." }
                    val buffer = ByteArray(Config.BUFFER_SIZE)
                    val length = externalIo.first.read(buffer)
                    val data = if (length > 0) buffer.copyOfRange(0, length) else ByteArray(0)
                    logger.trace { "Data returned." }

                    return OnionRelayCell(cell.circuitId, OnionRelayCommand.DATA, data)
                }

                // This method uses external io, not target io.
                OnionRelayCommand.BEGIN -> {
                    if (targetSocket != null) {
                        throw IllegalStateException("There is already an established connection")
                    }

                    val info = SocketInfo.deserialize(cell.data)
                    targetSocket = SocketUtil.getSocket(info.address, info.port)
                    externalIo = SocketUtil.getSocketStreams(targetSocket!!)
                    targetIsRouter = false

                    return OnionRelayCell(cell.circuitId, OnionRelayCommand.BEGIN, ByteArray(0))
                }

                // This method uses external io, not target io.
                OnionRelayCommand.END -> {
                    if (targetSocket == null || targetIsRouter) {
                        throw IllegalStateException("There is no remote connection to end")
                    }

                    externalIo.first.close()
                    externalIo.second.close()
                    targetSocket?.close()

                    return OnionRelayCell(cell.circuitId, OnionRelayCommand.END, ByteArray(0))
                }

                OnionRelayCommand.EXTEND -> {
                    // Find and connect the router to extend to using the id in the first byte.
                    val router = Config.ONION_ROUTER_DIRECTORY.getRouter(cell.data[0].toInt())
                    targetIsRouter = true
                    targetSocket = SocketUtil.getSocket(router.address, router.port)
                    targetIo = SocketUtil.getSocketIO(targetSocket!!)

                    // The remaining data is the create request that should be written to the new router.
                    val requestData = ByteArrayUtil.removeByteFromFront(cell.data)
                    writeByteArray(targetIo.second, requestData)

                    // Get the response data from the new router.
                    val responseData = readByteArray(targetIo.first)
                    logger.debug { "Writing extend response to client." }
                    return OnionRelayCell(cell.circuitId, OnionRelayCommand.EXTEND, responseData)
                }

                OnionRelayCommand.TRUNCATE -> {
                    // The remaining data is the create request that should be written to the new router.
                    writeByteArray(targetIo.second, cell.data)

                    // Get the response data from the new router.
                    val responseData = readByteArray(targetIo.first)
                    val response = OnionUtil.deserializeCell(responseData)

                    if (response.command == OnionControlCommand.DESTROY) {
                        targetIo.first.close()
                        targetIo.second.close()
                        targetSocket?.close()
                    }

                    logger.debug { "Writing truncate response to client." }
                    return OnionRelayCell(cell.circuitId, OnionRelayCommand.TRUNCATE, responseData)
                }
            }
        }

        /**
         * Reads byte array from reader. Routers use Base64 for communication.
         * @see Base64
         */
        private fun readByteArray(reader: BufferedReader): ByteArray {
            return Base64.getDecoder().decode(reader.readLine())
        }

        /**
         * Writes byte array to writer. Routers use Base64 for communication.
         * @see Base64
         */
        private fun writeByteArray(writer: PrintWriter, bytes: ByteArray) {
            writer.println(Base64.getEncoder().encodeToString(bytes))
            writer.flush()
        }
    }
}
