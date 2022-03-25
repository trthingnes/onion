package edu.ntnu.tobiasth.onionrouting.socks

import edu.ntnu.tobiasth.onionrouting.Config
import edu.ntnu.tobiasth.onionrouting.socks.handshake.SocksHandshakeMethod
import edu.ntnu.tobiasth.onionrouting.socks.handshake.SocksHandshakeRequest
import edu.ntnu.tobiasth.onionrouting.socks.handshake.SocksHandshakeResponse
import edu.ntnu.tobiasth.onionrouting.socks.request.SocksCommand
import edu.ntnu.tobiasth.onionrouting.socks.request.SocksReply
import edu.ntnu.tobiasth.onionrouting.socks.request.SocksRequest
import edu.ntnu.tobiasth.onionrouting.socks.request.SocksResponse
import edu.ntnu.tobiasth.onionrouting.util.OnionUtil
import edu.ntnu.tobiasth.onionrouting.util.SocketUtil
import mu.KotlinLogging
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlin.concurrent.thread

/**
 * Implementation of SOCKS5 protocol.
 */
class Socks5 : SocksProtocol {
    private val logger = KotlinLogging.logger {}

    override fun handleHandshake(input: InputStream, output: OutputStream) {
        logger.debug { "Reading handshake request from socket." }
        val request = SocksHandshakeRequest(input)

        val method = SocksHandshakeMethod.NO_AUTHENTICATION_REQUIRED
        logger.debug { "$method is the server method" }
        if (method !in request.methods) {
            throw IllegalArgumentException("Method $method is not in client handshake")
        }

        val response = SocksHandshakeResponse(method, request)
        logger.debug { "Writing handshake response to client." }
        response.toByteList().forEach { output.write(it) }
        output.flush()
    }

    override fun handleCommand(input: InputStream, output: OutputStream) {
        val request = SocksRequest(input)

        logger.info { "Performing command ${request.command}." }
        when (request.command) {
            SocksCommand.CONNECT -> {
                val remoteStreams = if (Config.ONION_ENABLED) {
                    SocketUtil.getOnionStreams(
                        OnionUtil.createCircuit(Config.ONION_CIRCUIT_SIZE, Config.ONION_ROUTER_DIRECTORY),
                        request.destAddress,
                        request.destPort
                    )
                } else {
                    SocketUtil.getSocketStreams(request.destAddress, request.destPort)
                }

                val response = SocksResponse(SocksReply.SUCCEEDED, Config.SOCKS_PORT)
                logger.debug { "Sending response to client." }
                write(output, response.toByteList())

                logger.info { "Starting data exchange." }
                exchangeData(input, output, remoteStreams.first, remoteStreams.second)
            }

            // ! SocksCommand.BIND -> {}
            // ! SocksCommand.UDP_ASSOCIATE -> {}

            else -> {
                val response = SocksResponse(SocksReply.COMMAND_NOT_SUPPORTED, Config.SOCKS_PORT)
                write(output, response.toByteList())
                throw IllegalStateException("Unsupported command in SOCKS request")
            }
        }
    }

    override fun exchangeData(clientIn: InputStream, clientOut: OutputStream, remoteIn: InputStream, remoteOut: OutputStream) {
        val thread = thread { // Start separate thread, so we have one for each direction of flow.
            forwardData(clientIn, "client", remoteOut, "server")
        }

        forwardData(remoteIn, "remote", clientOut, "client")

        thread.join()
    }

    /**
     * Forwards all data from given input stream to given output stream.
     */
    private fun forwardData(from: InputStream, fromName: String, to: OutputStream, toName: String) {
        val buffer = ByteArray(Config.BUFFER_SIZE)

        try {
            var byteCount = from.read(buffer)
            while (byteCount != -1) {
                logger.debug { "Read $byteCount bytes from $fromName." }
                to.write(buffer, 0, byteCount)
                logger.debug { "Wrote $byteCount to $toName." }
                to.flush()
                byteCount = from.read(buffer)
            }
        } catch (e: IOException) {
            logger.debug { "Connection from $fromName to $toName closed." }
            from.close()
            to.close()
        }
    }

    /**
     * Writes data to the output stream and flushes.
     */
    private fun write(target: OutputStream, data: List<Int>) {
        data.forEach { target.write(it) }
        target.flush()
    }
}
