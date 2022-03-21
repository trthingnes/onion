package edu.ntnu.tobiasth.onionproxy.util

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class EncryptionUtil {
    companion object {
        const val KEYALG = "AES"
        const val CIPHER = "AES/ECB/PKCS5Padding"

        fun getKeySpec(sharedSecret: ByteArray): SecretKeySpec {
            return SecretKeySpec(sharedSecret, KEYALG)
        }

        fun getEncryptCipher(key: SecretKeySpec): Cipher {
            val cipher = Cipher.getInstance(CIPHER)
            cipher.init(Cipher.ENCRYPT_MODE, key)
            return cipher
        }

        fun getDecryptCipher(key: SecretKeySpec): Cipher {
            val cipher = Cipher.getInstance(CIPHER)
            cipher.init(Cipher.DECRYPT_MODE, key)
            return cipher
        }

        fun useCipher(cipher: Cipher, data: ByteArray): ByteArray {
            return cipher.doFinal(data)
        }
    }
}