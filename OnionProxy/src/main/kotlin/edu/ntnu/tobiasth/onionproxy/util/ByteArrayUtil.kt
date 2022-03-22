package edu.ntnu.tobiasth.onionproxy.util

class ByteArrayUtil {
    companion object {
        fun addByteToFront(array: ByteArray, byte: Byte): ByteArray {
            val new = mutableListOf(byte)
            new.addAll(array.toList())
            return new.toByteArray()
        }

        fun removeByteFromFront(array: ByteArray): ByteArray {
            val new = array.toMutableList()
            new.removeFirst()
            return new.toByteArray()
        }
    }
}