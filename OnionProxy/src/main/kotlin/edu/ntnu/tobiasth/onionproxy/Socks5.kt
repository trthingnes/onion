package edu.ntnu.tobiasth.onionproxy

import edu.ntnu.tobiasth.onionproxy.socks.request.SocksRequest
import edu.ntnu.tobiasth.onionproxy.socks.handshake.SocksHandshakeMethod
import edu.ntnu.tobiasth.onionproxy.socks.handshake.SocksHandshakeRequest
import edu.ntnu.tobiasth.onionproxy.socks.handshake.SocksHandshakeResponse
import edu.ntnu.tobiasth.onionproxy.socks.request.SocksCommand
import edu.ntnu.tobiasth.onionproxy.socks.request.SocksReply
import edu.ntnu.tobiasth.onionproxy.socks.request.SocksResponse
import mu.KotlinLogging
import java.io.BufferedOutputStream
import java.io.DataInputStream

class Socks5: SocksProtocol {
    private val logger = KotlinLogging.logger {}

    override fun performHandshake(input: DataInputStream, output: BufferedOutputStream) {
        logger.debug { "Reading handshake request from socket." }

        val request = SocksHandshakeRequest(input)
        val method = SocksHandshakeMethod.NO_AUTHENTICATION_REQUIRED
        logger.debug { "$method is the server method." }

        if (method !in request.methods) {
            throw IllegalArgumentException("Method $method is not in client handshake.")
        }

        val response = SocksHandshakeResponse(method, request)
        logger.debug { "Writing handshake response to client." }
        response.toByteList().forEach { output.write(it) }
        output.flush()
    }

    override fun handleCommand(input: DataInputStream, output: BufferedOutputStream) {
        val request = SocksRequest(input)

        logger.debug { "Performing command ${request.command}." }
        when (request.command) {
            SocksCommand.CONNECT -> {
                // Open a new socket on a port that the client can connect to.
                // Open a connection to the desired server.
                // Return the proxy socket information.

                val response = SocksResponse(SocksReply.SUCCEEDED, 8888)
                response.toByteList().forEach { output.write(it) }
                output.flush()

                // ? Potentially do this in a separate thread.
            }

            SocksCommand.BIND -> {
                TODO()
            }

            SocksCommand.UDP_ASSOCIATE -> {
                TODO()
            }

            else -> {
                throw IllegalStateException("Unexpected command in SOCKS request.")
            }
        }
    }
}