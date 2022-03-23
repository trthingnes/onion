package edu.ntnu.tobiasth.onion.util

/**
 * Utility class for byte arrays.
 * @see ByteArray
 */
class ByteArrayUtil {
    companion object {
        /**
         * Adds a byte to the front of the byte array..
         * @param bytes Original byte array.
         * @param byte Byte to add.
         * @return Updated byte array.
         */
        fun addByteToFront(bytes: ByteArray, byte: Byte): ByteArray {
            val new = mutableListOf(byte)
            new.addAll(bytes.toList())
            return new.toByteArray()
        }

        /**
         * Removes the first byte from the front of the byte array.
         * @param bytes Original byte array.
         * @return Updated byte array.
         */
        fun removeByteFromFront(bytes: ByteArray): ByteArray {
            val new = bytes.toMutableList()
            new.removeFirst()
            return new.toByteArray()
        }
    }
}
