package edu.ntnu.tobiasth.onionproxy.util

import java.util.*

class Base64Util {
    companion object {
        fun encode(bytes: ByteArray): String {
            return Base64.getEncoder().encodeToString(bytes)
        }

        fun decode(string: String): ByteArray {
            return Base64.getDecoder().decode(string)
        }
    }
}