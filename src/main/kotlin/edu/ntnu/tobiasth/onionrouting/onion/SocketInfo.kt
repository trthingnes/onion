package edu.ntnu.tobiasth.onionrouting.onion

import java.io.*
import java.net.InetAddress

/**
 * Information about a socket (address and port).
 */
class SocketInfo(val address: InetAddress, val port: Int) : Serializable {
    companion object {
        /**
         * Deserialize socket info from byte array.
         * @param bytes Byte array to deserialize.
         * @return Socket info.
         */
        fun deserialize(bytes: ByteArray): SocketInfo {
            val bis = ByteArrayInputStream(bytes)
            val input = ObjectInputStream(bis)

            return input.readObject() as SocketInfo
        }
    }

    /**
     * Serialize cell to byte array.
     * @return Byte array of cell.
     */
    fun serialize(): ByteArray {
        val bos = ByteArrayOutputStream()
        val output = ObjectOutputStream(bos)

        output.writeObject(this)
        output.flush()

        return bos.toByteArray()
    }
}
