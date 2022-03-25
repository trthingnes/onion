package edu.ntnu.tobiasth.onionrouting.onion

import java.net.InetAddress

/**
 * Connection and encryption information for a router.
 */
class OnionRouterInfo(val address: InetAddress, val port: Int, var sharedSecret: ByteArray? = null) {
    /**
     * Returns whether a router is equal to another object or not.
     * @param other Object to compare to.
     * @return True if equal, false if not.
     */
    override fun equals(other: Any?): Boolean {
        if (other is OnionRouterInfo) {
            return address == other.address && port == other.port
        }
        return false
    }

    /**
     * Returns hashcode for router info.
     * @return Hashcode.
     */
    override fun hashCode(): Int {
        var result = address.hashCode()
        result = 31 * result + port
        return result
    }
}
