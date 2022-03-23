package edu.ntnu.tobiasth.onion.util

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * Utility class for symmetric encryption. Use Diffie Hellman to get shared secret.
 * @see DiffieHellmanUtil
 */
class EncryptionUtil {
    companion object {
        const val KEYALG = "AES"
        const val CIPHER = "AES/ECB/PKCS5Padding"

        /**
         * Gets a cipher from the shared secret that can be used to encrypt data.
         * @param sharedSecret Shared secret derived from key exchange.
         */
        fun getEncryptCipher(sharedSecret: ByteArray): Cipher {
            val key = SecretKeySpec(sharedSecret.copyOfRange(0, 32), KEYALG)
            val cipher = Cipher.getInstance(CIPHER)
            cipher.init(Cipher.ENCRYPT_MODE, key)
            return cipher
        }

        /**
         * Gets a cipher from the shared secret that can be used to decrypt data.
         * @param sharedSecret Shared secret derived from key exchange.
         */
        fun getDecryptCipher(sharedSecret: ByteArray): Cipher {
            val key = SecretKeySpec(sharedSecret.copyOfRange(0, 32), KEYALG)
            val cipher = Cipher.getInstance(CIPHER)
            cipher.init(Cipher.DECRYPT_MODE, key)
            return cipher
        }

        /**
         * Uses the given cipher on the given data.
         * @param cipher Cipher to use.
         * @param data Data to use cipher on.
         * @return Data after using cipher.
         */
        fun useCipher(cipher: Cipher, data: ByteArray): ByteArray {
            return cipher.doFinal(data)
        }
    }
}
