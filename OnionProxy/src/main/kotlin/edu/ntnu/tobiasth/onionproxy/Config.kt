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
        const val ONION_CIRCUIT_SIZE: Int = 1
        const val BUFFER_SIZE: Int = 4096

        val PROXY_KEY: KeyPair = DiffieHellmanUtil.getKeyPair()
        val ROUTER_DIRECTORY: OnionRouterDirectory = OnionRouterDirectory(listOf(
            OnionRouterInfo(InetAddress.getLoopbackAddress(), 1111),
            OnionRouterInfo(InetAddress.getLoopbackAddress(), 2222),
            OnionRouterInfo(InetAddress.getLoopbackAddress(), 3333),
        ))
    }
}