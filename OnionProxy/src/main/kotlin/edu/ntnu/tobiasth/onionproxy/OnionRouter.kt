package edu.ntnu.tobiasth.onionproxy

import edu.ntnu.tobiasth.onionproxy.onion.cell.*
import edu.ntnu.tobiasth.onionproxy.util.*
import mu.KotlinLogging
import java.io.BufferedReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.security.KeyPair
import kotlin.concurrent.thread

class OnionRouter(port: Int) {
    private val logger = KotlinLogging.logger {}
    private val keypair = DiffieHellmanUtil.getKeyPair()

    init {
        val server = ServerSocket(port)
        logger.info { "Waiting for connections on port ${port}." }

        while(true) {
            val socket = server.accept()
            thread {
                try {
                    logger.info { "New connection from client at ${socket.inetAddress}:${socket.port}." }
                    ClientHandler(socket, keypair)
                }
                catch (e: Exception) {
                    logger.error { "An error occured with the message '${e.message}'." }
                    e.printStackTrace() //TODO: Remove
                    logger.debug { "Closing socket." }
                    socket.close()
                }
            }
        }
    }

    class ClientHandler(private val clientSocket: Socket, private val keypair: KeyPair) {
        private val logger = KotlinLogging.logger {}
        private val clientIo: Pair<BufferedReader, PrintWriter> = SocketUtil.getSocketIO(clientSocket)
        private lateinit var routerSocket: Socket
        private lateinit var routerIo: Pair<BufferedReader, PrintWriter>
        private lateinit var sharedSecret: ByteArray

        init {
            handleHandshake()

            while(!clientSocket.isClosed) {
                handleRequest()
            }
        }

        private fun handleHandshake() {
            val handshake = SerializeUtil.deserialize(readByteArray(clientIo.first))
            logger.debug { "Received handshake with id ${handshake.circuitId}." }

            if(handshake.command != OnionControlCommand.CREATE) {
                throw IllegalStateException("Expected command CREATE, but got ${handshake.command}")
            }

            val publicKey = DiffieHellmanUtil.getPublicKeyFromEncoded(handshake.data)
            sharedSecret = DiffieHellmanUtil.getSharedSecret(keypair.private, publicKey, publicKey.params)
            logger.debug { "Established shared secret." }

            val response = OnionControlCell(handshake.circuitId, OnionControlCommand.CREATE, keypair.public.encoded)
            logger.debug { "Writing handshake response." }
            writeByteArray(clientIo.second, SerializeUtil.serialize(response))
        }

        private fun handleRequest() {
            logger.debug { "Waiting to read cell." }
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

        private fun handleControlCell(cell: OnionControlCell): OnionControlCell {
            when(cell.command) {
                OnionControlCommand.DESTROY -> {
                    return OnionControlCell(cell.circuitId, OnionControlCommand.DESTROY, ByteArray(0))
                }
                else -> {
                    throw IllegalStateException("Unexpected command ${cell.command}")
                }
            }
        }

        private fun handleRelayCell(cell: OnionRelayCell): OnionRelayCell {
            when(cell.relayCommand) {
                OnionRelayCommand.RELAY -> {
                    writeByteArray(routerIo.second, cell.data)
                    return OnionRelayCell(cell.circuitId, OnionRelayCommand.RELAY, readByteArray(routerIo.first))
                }

                OnionRelayCommand.DATA -> TODO()
                OnionRelayCommand.BEGIN -> TODO()
                OnionRelayCommand.END -> TODO()

                OnionRelayCommand.EXTEND -> {
                    // Find and connect the router to extend to using the id in the first byte.
                    val router = Config.ONION_ROUTER_DIRECTORY.getRouter(cell.data[0].toInt())
                    routerSocket = SocketUtil.getSocket(router.address, router.port)
                    routerIo = SocketUtil.getSocketIO(routerSocket)

                    // The remaining data is the create request that should be written to the new router.
                    val requestData = ByteArrayUtil.removeByteFromFront(cell.data)
                    writeByteArray(routerIo.second, requestData)

                    // Get the response data from the new router.
                    val responseData = readByteArray(routerIo.first)
                    logger.debug { "Writing extend response to client." }
                    return OnionRelayCell(cell.circuitId, OnionRelayCommand.EXTEND, responseData)
                }

                OnionRelayCommand.TRUNCATE -> TODO()
            }
        }

        private fun readByteArray(reader: BufferedReader): ByteArray {
            return Base64Util.decode(reader.readLine())
        }

        private fun writeByteArray(writer: PrintWriter, bytes: ByteArray) {
            writer.println(Base64Util.encode(bytes))
            writer.flush()
        }
    }
}