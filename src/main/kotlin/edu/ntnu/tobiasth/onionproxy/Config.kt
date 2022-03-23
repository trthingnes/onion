package edu.ntnu.tobiasth.onionproxy

import edu.ntnu.tobiasth.onionproxy.onion.OnionRouterDirectory
import edu.ntnu.tobiasth.onionproxy.onion.OnionRouterInfo
import edu.ntnu.tobiasth.onionproxy.util.DiffieHellmanUtil
import java.net.InetAddress
import java.security.KeyPair

class Config {
    companion object {
        const val SOCKS_PORT: Int = 1080
        const val SOCKS_VERSION: Int = 5
        const val ONION_ENABLED: Boolean = false
        const val ONION_CIRCUIT_SIZE: Int = 3
        const val BUFFER_SIZE: Int = 4096

        val ONION_PROXY_KEY: KeyPair = DiffieHellmanUtil.getKeyPair()
        val ONION_ROUTER_PORTS: List<Int> = listOf(1111, 2222, 3333, 4444, 5555)
        val ONION_ROUTER_DIRECTORY: OnionRouterDirectory = OnionRouterDirectory(
            ONION_ROUTER_PORTS.map { OnionRouterInfo(InetAddress.getLoopbackAddress(), it) }
        )
    }
}
