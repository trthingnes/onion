package edu.ntnu.tobiasth.onionproxy.util

import edu.ntnu.tobiasth.onionproxy.onion.OnionCircuit
import edu.ntnu.tobiasth.onionproxy.onion.OnionRouterDirectory
import edu.ntnu.tobiasth.onionproxy.onion.OnionRouterInfo
import edu.ntnu.tobiasth.onionproxy.onion.cell.OnionCell
import java.util.*
import kotlin.NoSuchElementException

class OnionUtil {
    companion object {
        /**
         * Create a random circuit of the given size from the given router directory.
         */
        fun createCircuit(size: Int, directory: OnionRouterDirectory): OnionCircuit {
            val routers = arrayListOf<OnionRouterInfo>()
            routers.addAll(directory.routers)

            val firstRouter = routers.random()
            routers.remove(firstRouter)
            val circuit = OnionCircuit(UUID.randomUUID(), firstRouter)

            try {
                repeat(size - 1) {
                    val randomRouter = routers.random()
                    routers.remove(randomRouter)
                    circuit.extend(randomRouter)
                }
            }
            catch(e: NoSuchElementException) {
                throw IllegalStateException("Not enough routers to create circuit of size $size")
            }

            return circuit
        }

        /**
         * Decrypt and deserialize the given data using the given shared secret.
         */
        fun decryptCell(data: ByteArray, sharedSecret: ByteArray): OnionCell {
            val cipher = EncryptionUtil.getDecryptCipher(sharedSecret)
            val serializedData = EncryptionUtil.useCipher(cipher, data)

            return SerializeUtil.deserialize(serializedData)
        }

        /**
         * Serialize and encrypt the given cell by using the given shared secret.
         */
        fun encryptCell(cell: OnionCell, sharedSecret: ByteArray): ByteArray {
            val cipher = EncryptionUtil.getEncryptCipher(sharedSecret)
            val serializedData = SerializeUtil.serialize(cell)

            return EncryptionUtil.useCipher(cipher, serializedData)
        }
    }
}