package edu.ntnu.tobiasth.onionproxy.socks.handshake

import edu.ntnu.tobiasth.onionproxy.Config
import edu.ntnu.tobiasth.onionproxy.Request
import mu.KotlinLogging
import java.io.Reader


/**
 * An initial SOCKS handshake request.
 * Expected data structure: [<Version - 1B> <Count - 1B - 1-255> <Methods - Count*1B>].
 */
class HandshakeRequest(reader: Reader): Request {
    private val logger = KotlinLogging.logger {}

    var methods: ArrayList<HandshakeMethod>

    init {
        // Make sure the SOCKS version given is correct.
        if (reader.read().compareTo(Config.SOCKS_VERSION) != 0) {
            throw IllegalArgumentException("Invalid SOCKS version.")
        }

        // Find the number of methods in the request.
        val methodCount = reader.read()

        methods = arrayListOf()
        logger.debug { "Request has $methodCount methods." }

        // Retrieve all methods in request.
        for (i in (2 until methodCount + 2)) {
            val method = HandshakeMethod.of(reader.read())
            methods.add(method)
            logger.debug { "[${i - 2}] $method" }
        }
    }
}