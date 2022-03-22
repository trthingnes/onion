package edu.ntnu.tobiasth.onionproxy.socks.request

/**
 * Commands a SOCKS client can give to the server.
 */
enum class SocksCommand(val code: Int) {
    CONNECT(0x01),
    BIND(0x02),
    UDP_ASSOCIATE(0x03),
    UNKNOWN(0xFF);

    companion object {
        fun of(code: Int): SocksCommand {
            return values().first { c -> c.code == code }
        }
    }
}