package edu.ntnu.tobiasth.onionproxy.onion

import java.net.InetAddress

class OnionRouterInfo(val address: InetAddress, val port: Int, var sharedSecret: ByteArray? = null) {
    override fun equals(other: Any?): Boolean {
        if(other is OnionRouterInfo) {
            return address == other.address && port == other.port
        }
        return false
    }
}