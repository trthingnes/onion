package edu.ntnu.tobiasth.onionproxy.util

import edu.ntnu.tobiasth.onionproxy.onion.cell.OnionCell
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class SerializeUtil {
    companion object {
        fun serialize(cell: OnionCell): ByteArray {
            val bos = ByteArrayOutputStream()
            val output = ObjectOutputStream(bos)

            output.writeObject(cell)
            output.flush()

            return bos.toByteArray()
        }

        fun deserialize(bytes: ByteArray): OnionCell {
            val bis = ByteArrayInputStream(bytes)
            val input = ObjectInputStream(bis)

            return input.readObject() as OnionCell
        }
    }
}
