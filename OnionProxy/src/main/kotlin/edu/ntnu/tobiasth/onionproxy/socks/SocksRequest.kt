package edu.ntnu.tobiasth.onionproxy.socks

import edu.ntnu.tobiasth.onionproxy.Request

class SocksRequest: Request {
    enum class Command(code: Int) {
        CONNECT(0x01),
        BIND(0x02),
        UDP_ASSOCIATE(0x03),
        UNKNOWN(0xFF)
    }
}