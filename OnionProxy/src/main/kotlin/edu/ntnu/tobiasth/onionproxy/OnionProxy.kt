package edu.ntnu.tobiasth.onionproxy

import edu.ntnu.tobiasth.onionproxy.socks.handshake.HandshakeMethod
import edu.ntnu.tobiasth.onionproxy.socks.handshake.HandshakeRequest
import edu.ntnu.tobiasth.onionproxy.socks.handshake.HandshakeResponse
import mu.KotlinLogging
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

class OnionProxy {
    private val logger = KotlinLogging.logger {}

    init {
        val server = ServerSocket(Config.SOCKS_PORT)
        logger.info { "Waiting for connections on port ${Config.SOCKS_PORT}." }

        while(true) {
            val socket = server.accept()
            thread {
                try {
                    handleClient(socket)
                }
                catch (e: Exception) {
                    logger.error { "An error occured with the message '${e.message}'." }
                    socket.close()
                }
            }
        }
    }

    private fun handleClient(socket: Socket) {
        logger.debug { "Got a connection from ${socket.inetAddress}:${socket.port}." }
        val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
        val writer = PrintWriter(socket.getOutputStream(), true)

        performSocksHandshake(reader, writer)
    }

    private fun performSocksHandshake(reader: BufferedReader, writer: PrintWriter) {
        logger.debug { "Reading handshake request from socket." }

        val bytes = arrayListOf<Byte>()
        while(reader.ready()) {
            bytes.add(reader.read().toByte())
        }

        val request = HandshakeRequest(bytes.toByteArray())
        val method = HandshakeMethod.NO_AUTHENTICATION_REQUIRED

        if (method !in request.methods) {
            throw IllegalArgumentException("Method ${method} is not in client handshake.")
        }

        logger.debug { "$method is the server method." }

        val response = HandshakeResponse(request, method)

        logger.debug { "Writing handshake response to client." }
        writer.write(String(response.toByteArray()))
        writer.flush()
    }

    private fun handleCommand(reader: BufferedReader, writer: PrintWriter) {

    }
}