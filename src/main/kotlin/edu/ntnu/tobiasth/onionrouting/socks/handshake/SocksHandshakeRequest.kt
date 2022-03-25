package edu.ntnu.tobiasth.onionrouting.socks.handshake

import edu.ntnu.tobiasth.onionrouting.Config
import mu.KotlinLogging
import java.io.InputStream

/**
 * An initial SOCKS handshake request.
 */
class SocksHandshakeRequest(input: InputStream) {
    private val logger = KotlinLogging.logger {}
    val methods: ArrayList<SocksHandshakeMethod>

    init {
        // Make sure the SOCKS version given is correct.
        if (input.read().compareTo(Config.SOCKS_VERSION) != 0) {
            throw IllegalArgumentException("Invalid SOCKS version")
        }

        // Find the number of methods in the request.
        val methodCount = input.read()

        methods = arrayListOf()
        logger.debug { "Request has $methodCount methods." }

        // Retrieve all methods in request.
        for (i in (2 until methodCount + 2)) {
            val method = SocksHandshakeMethod.of(input.read())
            methods.add(method)
            logger.debug { "[${i - 2}] $method" }
        }
    }
}
