package edu.ntnu.tobiasth.onionproxy.socks

import edu.ntnu.tobiasth.onionproxy.Config
import edu.ntnu.tobiasth.onionproxy.socks.request.SocksRequest
import edu.ntnu.tobiasth.onionproxy.socks.handshake.SocksHandshakeMethod
import edu.ntnu.tobiasth.onionproxy.socks.handshake.SocksHandshakeRequest
import edu.ntnu.tobiasth.onionproxy.socks.handshake.SocksHandshakeResponse
import edu.ntnu.tobiasth.onionproxy.socks.request.SocksCommand
import edu.ntnu.tobiasth.onionproxy.socks.request.SocksReply
import edu.ntnu.tobiasth.onionproxy.socks.request.SocksResponse
import edu.ntnu.tobiasth.onionproxy.util.SocketUtil
import mu.KotlinLogging
import java.io.*
import kotlin.concurrent.thread

class Socks5: SocksProtocol {
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
                val remoteStreams = SocketUtil.getSocketStreams(request.destAddress, request.destPort)

                val response = SocksResponse(SocksReply.SUCCEEDED, Config.SOCKS_PORT)
                logger.debug { "Sending response to client." }
                write(output, response.toByteList())

                logger.info { "Starting data exchange." }
                exchangeData(input, output, remoteStreams.first, remoteStreams.second)
            }

            // TODO: SocksCommand.BIND {}
            // TODO: SocksCommand.UDP_ASSOCIATE {}

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

    private fun forwardData(from: InputStream, fromName: String, to: OutputStream, toName: String) {
        val buffer = ByteArray(Config.BUFFER_SIZE)

        try {
            var byteCount = from.read(buffer)
            while(byteCount != -1) {
                logger.debug { "Read $byteCount bytes from $fromName." }
                to.write(buffer, 0, byteCount)
                logger.debug { "Wrote $byteCount to $toName." }
                to.flush()
                byteCount = from.read(buffer)
            }
        }
        catch(e: IOException) {
            logger.debug { "Connection from $fromName to $toName closed." }
            from.close()
            to.close()
        }
    }

    private fun write(target: OutputStream, data: List<Int>) {
        data.forEach { target.write(it) }
        target.flush()
    }
}