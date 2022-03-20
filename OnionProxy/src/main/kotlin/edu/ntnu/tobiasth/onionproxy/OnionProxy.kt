package edu.ntnu.tobiasth.onionproxy

import edu.ntnu.tobiasth.onionproxy.util.SocketUtil
import mu.KotlinLogging
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
                    logger.debug { "Connection to client has been closed." }
                }
            }
        }
    }

    private fun handleClient(socket: Socket) {
        logger.debug { "Got a connection from ${socket.inetAddress}:${socket.port}." }

        val input = SocketUtil.getInput(socket)
        val output = SocketUtil.getOutput(socket)

        socks.performHandshake(input, output)
        socks.handleCommand(input, output)
    }
}