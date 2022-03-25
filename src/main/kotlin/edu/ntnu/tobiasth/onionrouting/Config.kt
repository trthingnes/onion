package edu.ntnu.tobiasth.onionrouting

import edu.ntnu.tobiasth.onionrouting.onion.OnionRouterDirectory
import edu.ntnu.tobiasth.onionrouting.onion.OnionRouterInfo
import edu.ntnu.tobiasth.onionrouting.util.DiffieHellmanUtil
import java.net.InetAddress
import java.security.KeyPair

/**
 * Configuration class to change program behavior.
 */
class Config {
    companion object {
        const val ONION_ENABLED: Boolean = true
        const val ONION_CIRCUIT_SIZE: Int = 5
        const val SOCKS_PORT: Int = 1080
        const val SOCKS_VERSION: Int = 5
        const val BUFFER_SIZE: Int = 50000

        val ONION_PROXY_KEY: KeyPair = DiffieHellmanUtil.getKeyPair()
        val ONION_ROUTER_PORTS: List<Int> = listOf(1111, 2222, 3333, 4444, 5555)
        val ONION_ROUTER_DIRECTORY: OnionRouterDirectory = OnionRouterDirectory(
            ONION_ROUTER_PORTS.map { OnionRouterInfo(InetAddress.getLoopbackAddress(), it) }
        )
    }
}
