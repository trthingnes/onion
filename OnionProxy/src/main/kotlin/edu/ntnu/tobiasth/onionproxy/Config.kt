package edu.ntnu.tobiasth.onionproxy

class Config {
    companion object {
        const val SOCKS_PORT: Int = 1080
        const val SOCKS_VERSION: Int = 5
        const val ONION_ENABLED: Boolean = false
        const val BUFFER_SIZE: Int = 4096
    }
}