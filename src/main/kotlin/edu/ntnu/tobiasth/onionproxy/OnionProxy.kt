package edu.ntnu.tobiasth.onionproxy

import edu.ntnu.tobiasth.onionproxy.socks.Socks5
import edu.ntnu.tobiasth.onionproxy.util.SocketUtil
import mu.KotlinLogging
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

// * To test the proxy without the onion routing enabled, run this.
fun main() {
    if (!Config.ONION_ENABLED) {
        OnionProxy()
    } else {
        println("Disable onion features in config to run the proxy separately.")
    }
}

// ? Test with curl? Use: curl -x socks5://127.0.0.1[:port] http://datakom.no
/**
 * Proxy that accepts connections from local applications and forwards them.
 * Depending on the config the traffic is either routed through onion circuit or connected directly.
 * @see Config
 * @see Socks5
 */
class OnionProxy {
    private val logger = KotlinLogging.logger {}
    private val socks = when (Config.SOCKS_VERSION) {
        5 -> Socks5()
        else -> { throw NotImplementedError("Socks version ${Config.SOCKS_VERSION} not supported.") }
    }

    init {
        val server = ServerSocket(Config.SOCKS_PORT)
        logger.info { "Waiting for connections on port ${Config.SOCKS_PORT}." }

        while (true) {
            val socket = server.accept()
            thread {
                try {
                    logger.info { "New connection from client at ${socket.inetAddress}:${socket.port}." }
                    handleClient(socket)
                } catch (e: Exception) {
                    logger.error { "An error occured with the message '${e.message}'." }
                } finally {
                    socket.close()
                    logger.info { "Connection to client has been closed." }
                }
            }
        }
    }

    /**
     * Handles client by doing handshake and command.
     */
    private fun handleClient(socket: Socket) {
        val streams = SocketUtil.getSocketStreams(socket)
        val input = streams.first
        val output = streams.second

        socks.handleHandshake(input, output)
        socks.handleCommand(input, output)
    }
}
