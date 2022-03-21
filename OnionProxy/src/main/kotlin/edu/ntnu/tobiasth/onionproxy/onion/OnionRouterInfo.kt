package edu.ntnu.tobiasth.onionproxy.onion

import java.net.InetAddress

class OnionRouterInfo(val address: InetAddress, val port: Int, var sharedSecret: ByteArray? = null)