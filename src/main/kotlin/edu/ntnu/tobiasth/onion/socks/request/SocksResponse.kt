package edu.ntnu.tobiasth.onion.socks.request

import edu.ntnu.tobiasth.onion.Config
import mu.KotlinLogging

/**
 * A SOCKS command response.
 * This is used after initial handshake.
 */
class SocksResponse(val reply: SocksReply, val port: Int) {
    private val logger = KotlinLogging.logger {}

    init {
        logger.debug { "Creating the response (Version: ${Config.SOCKS_VERSION}, Reply: $reply, Port: $port)." }
    }

    /**
     * Converts the response into a list of bytes to use with streams.
     * @return List of bytes (has to be given as int because of Java).
     */
    fun toByteList(): List<Int> {
        return listOf(
            Config.SOCKS_VERSION, // Version
            reply.code, // Reply code
            0x00, // Reserved byte
            SocksAddressType.IPV4.code, // Address type
            127, 0, 0, 1, // Address
            port shr 8, // Port byte 1
            port and 0xFF // Port byte 2
        )
    }
}
