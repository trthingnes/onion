package edu.ntnu.tobiasth.onionproxy.socks.handshake

import edu.ntnu.tobiasth.onionproxy.Config
import edu.ntnu.tobiasth.onionproxy.Response
import mu.KotlinLogging

/**
 * An initial SOCKS handshake response.
 * Data structure: [<Version - 1B> <Method - 1B - 1-255>].
 */
class HandshakeResponse(private val request: HandshakeRequest, private val method: HandshakeMethod): Response {
    private val logger = KotlinLogging.logger {}
    private val methodIndex = (request.methods.indexOf(method)).toByte()

    init {
        logger.debug { "Creating the response (Version: ${Config.SOCKS_VERSION}, Method: [$methodIndex] $method)." }
    }

    fun toByteArray(): ByteArray {
        return listOf(Config.SOCKS_VERSION, methodIndex).toByteArray()
    }
}