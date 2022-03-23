package edu.ntnu.tobiasth.onionproxy.socks.request

/**
 * Types of IP addresses a SOCKS remote can have.
 */
enum class SocksAddressType(val code: Int, val size: Int) {
    IPV4(0x01, 4),
    DOMAIN_NAME(0x03, 1),
    IPV6(0x04, 16);

    companion object {
        fun of(code: Int): SocksAddressType {
            return values().first { a -> a.code == code }
        }
    }
}