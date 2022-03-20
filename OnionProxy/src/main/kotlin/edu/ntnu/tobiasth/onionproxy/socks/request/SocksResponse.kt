package edu.ntnu.tobiasth.onionproxy.socks.request

import edu.ntnu.tobiasth.onionproxy.Config
import edu.ntnu.tobiasth.onionproxy.Response
import mu.KotlinLogging

class SocksResponse(val reply: SocksReply, val port: Int): Response {
    private val logger = KotlinLogging.logger {}

    init {
        logger.debug { "Creating the response (Version: ${Config.SOCKS_VERSION}, Reply: $reply, Port: $port)." }
    }

    override fun toByteList(): List<Int> {
        return listOf(
            Config.SOCKS_VERSION, // Version
            reply.code, // Reply code
            0x00, // Reserved byte
            SocksAddressType.IPV4.code, // Address type
            127, 0, 0, 1, // Address
            port shr 8,
            port and 0xFF
        )
    }
}