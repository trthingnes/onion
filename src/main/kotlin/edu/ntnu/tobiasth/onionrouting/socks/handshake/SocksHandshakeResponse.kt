package edu.ntnu.tobiasth.onionrouting.socks.handshake

import edu.ntnu.tobiasth.onionrouting.Config
import mu.KotlinLogging

/**
 * An initial SOCKS handshake response.
 */
class SocksHandshakeResponse(private val method: SocksHandshakeMethod, request: SocksHandshakeRequest) {
    private val logger = KotlinLogging.logger {}
    private val methodIndex = (request.methods.indexOf(method))

    init {
        logger.debug { "Creating the response (Version: ${Config.SOCKS_VERSION}, Method: [$methodIndex] $method)." }
    }

    /**
     * Converts the response into a list of bytes to use with streams.
     * @return List of bytes (has to be given as int because of Java).
     */
    fun toByteList(): List<Int> {
        return listOf(Config.SOCKS_VERSION, methodIndex)
    }
}
