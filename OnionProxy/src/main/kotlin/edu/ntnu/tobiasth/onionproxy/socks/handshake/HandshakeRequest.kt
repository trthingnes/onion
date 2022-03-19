package edu.ntnu.tobiasth.onionproxy.socks.handshake

import edu.ntnu.tobiasth.onionproxy.Config
import edu.ntnu.tobiasth.onionproxy.Request
import mu.KotlinLogging

const val VERSION_BYTE = 0
const val COUNT_BYTE = 1

/**
 * An initial SOCKS handshake request.
 * Expected data structure: [<Version - 1B> <Count - 1B - 1-255> <Methods - Count*1B>].
 */
class HandshakeRequest(bytes: ByteArray): Request {
    private val logger = KotlinLogging.logger {}
    var methods: ArrayList<HandshakeMethod>

    init {
        // Make sure the SOCKS version given is correct.
        if (bytes[VERSION_BYTE].compareTo(Config.SOCKS_VERSION) != 0) {
            throw IllegalArgumentException("Invalid SOCKS version.")
        }

        // We need at least 3 bytes for a successful handshake: version + size + method.
        if (bytes.size < 3) {
            throw IllegalArgumentException("Too few bytes for a handshake request.")
        }

        // Find the number of methods in the request, and make sure we have one byte for each.
        val methodCount = bytes[COUNT_BYTE]
        if (bytes.size - 2 < methodCount) {
            throw IllegalArgumentException("Too few bytes for the number of methods.")
        }

        methods = arrayListOf()
        logger.debug { "Request has $methodCount methods." }

        // Retrieve all methods in request.
        for (i in (2 until methodCount + 2)) {
            val method = HandshakeMethod.of(bytes[i].toInt())
            methods.add(method)
            logger.debug { "[${i - 2}] $method" }
        }
    }
}