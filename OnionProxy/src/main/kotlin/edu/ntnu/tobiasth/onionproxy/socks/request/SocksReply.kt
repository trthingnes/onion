package edu.ntnu.tobiasth.onionproxy.socks.request

enum class SocksReply(val code: Int) {
    SUCCEEDED(0x00),
    SOCKS_SERVER_FAILURE(0x01),
    NOT_ALLOWED(0x02),
    NETWORK_UNREACHABLE(0x03),
    HOST_UNREACHABLE(0x04),
    CONNECTION_REFUSED(0x05),
    TTL_EXPIRED(0x06),
    COMMAND_NOT_SUPPORTED(0x07),
    ADDRESS_TYPE_NOT_SUPPORTED(0x08),
    UNKNOWN(0xFF)
}