package edu.ntnu.tobiasth.onionproxy

import edu.ntnu.tobiasth.onionproxy.socks.Socks5
import edu.ntnu.tobiasth.onionproxy.util.SocketUtil
import mu.KotlinLogging
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

fun main() {
    OnionProxy()
}

// ? Test with curl? Use: curl -x socks5://127.0.0.1[:port] http://datakom.no
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
                    logger.info { "New connection from client at ${socket.inetAddress}:${socket.port}." }
                    handleClient(socket)
                }
                catch (e: Exception) {
                    logger.error { "An error occured with the message '${e.message}'." }
                }
                finally {
                    socket.close()
                    logger.info { "Connection to client has been closed." }
                }
            }
        }
    }

    private fun handleClient(socket: Socket) {
        val input = SocketUtil.getDirectInput(socket)
        val output = SocketUtil.getDirectOutput(socket)

        socks.handleHandshake(input, output)
        socks.handleCommand(input, output)
    }
}