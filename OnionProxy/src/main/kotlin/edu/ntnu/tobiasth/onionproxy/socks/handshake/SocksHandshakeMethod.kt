package edu.ntnu.tobiasth.onionproxy.socks.handshake

enum class SocksHandshakeMethod(val code: Int) {
    NO_AUTHENTICATION_REQUIRED(0x00),
    GSSAPI(0x01),
    USERNAME_PASSWORD(0x02),
    FIRST_IANA_ASSIGNED(0x03),
    LAST_IANA_ASSIGNED(0x7F),
    FIRST_RESERVED_PRIVATE(0x80),
    LAST_RESERVED_PRIVATE(0xFE),
    NO_ACCEPTABLE_METHODS(0xFF);

    companion object {
        fun of(code: Int): SocksHandshakeMethod {
            return values().first { m -> m.code == code }
        }
    }
}