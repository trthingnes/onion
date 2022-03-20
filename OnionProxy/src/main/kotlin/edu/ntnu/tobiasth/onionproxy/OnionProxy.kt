package edu.ntnu.tobiasth.onionproxy

import mu.KotlinLogging
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

// ? Test with curl: curl -x socks5://127.0.0.1:1080 http://datakom.no
class OnionProxy {
    private val logger = KotlinLogging.logger {}
    private val socks = when (Config.SOCKS_VERSION) {
        5 -> Socks5()
        else -> { throw NotImplementedError("Socks version ${Config.SOCKS_VERSION} not supported.") }
    }

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
                }
                finally {
                    socket.close()
                }
            }
        }
    }

    private fun handleClient(socket: Socket) {
        logger.debug { "Got a connection from ${socket.inetAddress}:${socket.port}." }

        val input = DataInputStream(socket.getInputStream())
        val writer = PrintWriter(socket.getOutputStream(), true)

        socks.performHandshake(input, writer)

        while(!socket.isClosed) {
            socks.handleCommand(input, writer)
        }

        logger.debug { "Connection to client has been closed." }
    }
}