package edu.ntnu.tobiasth.onionproxy.util

class ByteArrayUtil {
    companion object {
        fun addByteToFront(bytes: ByteArray, byte: Byte): ByteArray {
            val new = mutableListOf(byte)
            new.addAll(bytes.toList())
            return new.toByteArray()
        }

        fun removeByteFromFront(bytes: ByteArray): ByteArray {
            val new = bytes.toMutableList()
            new.removeFirst()
            return new.toByteArray()
        }
    }
}
