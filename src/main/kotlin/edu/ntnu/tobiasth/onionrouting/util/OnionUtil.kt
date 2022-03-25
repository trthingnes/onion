package edu.ntnu.tobiasth.onionrouting.util

import edu.ntnu.tobiasth.onionrouting.onion.OnionCircuit
import edu.ntnu.tobiasth.onionrouting.onion.OnionRouterDirectory
import edu.ntnu.tobiasth.onionrouting.onion.OnionRouterInfo
import edu.ntnu.tobiasth.onionrouting.onion.cell.OnionCell
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*

/**
 * Utility class for onion related methods.
 */
class OnionUtil {
    companion object {
        /**
         * Create a random circuit of the given size from the given router directory.
         * @param size The number of routers wanted in the circuit.
         * @param directory The router directory to pick routers from.
         * @return A newly constructed circuit
         * @see OnionCircuit
         * @see OnionRouterDirectory
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
            } catch (e: NoSuchElementException) {
                throw IllegalStateException("Not enough routers to create circuit of size $size")
            }

            return circuit
        }

        /**
         * Decrypt and deserialize the given data using the given shared secret.
         * @param data Encrypted cell data.
         * @param sharedSecret Shared secret derived from key exchange.
         * @return Decrypted cell.
         * @see OnionCell
         * @see EncryptionUtil
         */
        fun decryptCell(data: ByteArray, sharedSecret: ByteArray): OnionCell {
            val cipher = EncryptionUtil.getDecryptCipher(sharedSecret)
            val serializedData = EncryptionUtil.useCipher(cipher, data)

            return deserializeCell(serializedData)
        }

        /**
         * Serialize and encrypt the given cell by using the given shared secret.
         * @param cell Cell to encrypt.
         * @param sharedSecret Shared secret derived from key exchange.
         * @return Encrypted cell data.
         * @see OnionCell
         * @see EncryptionUtil
         */
        fun encryptCell(cell: OnionCell, sharedSecret: ByteArray): ByteArray {
            val cipher = EncryptionUtil.getEncryptCipher(sharedSecret)
            val serializedData = serializeCell(cell)

            return EncryptionUtil.useCipher(cipher, serializedData)
        }

        /**
         * Serialize cell to byte array.
         * @param cell Cell to serialize.
         * @return Byte array of cell.
         * @see OnionCell
         */
        fun serializeCell(cell: OnionCell): ByteArray {
            val bos = ByteArrayOutputStream()
            val output = ObjectOutputStream(bos)

            output.writeObject(cell)
            output.flush()

            return bos.toByteArray()
        }

        /**
         * Deserialize cell from byte array.
         * @param bytes Byte array to deserialize.
         * @return Cell.
         * @see OnionCell
         */
        fun deserializeCell(bytes: ByteArray): OnionCell {
            val bis = ByteArrayInputStream(bytes)
            val input = ObjectInputStream(bis)

            return input.readObject() as OnionCell
        }
    }
}
