package edu.ntnu.tobiasth.onion.util

import java.security.*
import java.security.spec.X509EncodedKeySpec
import javax.crypto.KeyAgreement
import javax.crypto.interfaces.DHPrivateKey
import javax.crypto.interfaces.DHPublicKey
import javax.crypto.spec.DHParameterSpec

/**
 * Utility class for Diffie Hellman key exchange.
 * @see DHPublicKey
 * @see DHPrivateKey
 * @see DHParameterSpec
 */
class DiffieHellmanUtil {
    companion object {
        /**
         * Gets a shared secret derived from the given keys and spec.
         * @param privateKey Callers private key.
         * @param publicKey Public key of keyholder to share secret with.
         * @param spec Shared algorithm specifications.
         */
        fun getSharedSecret(privateKey: PrivateKey, publicKey: PublicKey, spec: DHParameterSpec): ByteArray {
            val keyAgreement = getKeyAgreement(privateKey, spec)
            keyAgreement.doPhase(publicKey, true)
            return keyAgreement.generateSecret()
        }

        /**
         * Generate a random algorithm specification.
         * @return Algorithm specification.
         */
        fun getDHParameterSpec(): DHParameterSpec {
            val key = getKeyPair().private as DHPrivateKey
            return key.params
        }

        /**
         * Get a randomly generated keypair.
         * @return DH keypair.
         */
        fun getKeyPair(): KeyPair {
            val keyPairGenerator = KeyPairGenerator.getInstance("DH")
            return keyPairGenerator.genKeyPair()
        }

        /**
         * Get public key from an encoded byte array.
         * @param bytes Public key as bytes.
         * @return DH public key.
         */
        fun getPublicKeyFromEncoded(bytes: ByteArray): DHPublicKey {
            val keyFactory = KeyFactory.getInstance("DH")
            return keyFactory.generatePublic(X509EncodedKeySpec(bytes)) as DHPublicKey
        }

        /**
         * Get private key from an encoded byte array.
         * @param bytes Private key as bytes.
         * @return DH private key.
         */
        fun getPrivateKeyFromEncoded(bytes: ByteArray): DHPrivateKey {
            val keyFactory = KeyFactory.getInstance("DH")
            return keyFactory.generatePrivate(X509EncodedKeySpec(bytes)) as DHPrivateKey
        }

        /**
         * Get key agreement from callers private key.
         */
        private fun getKeyAgreement(key: PrivateKey, spec: DHParameterSpec): KeyAgreement {
            val ka = KeyAgreement.getInstance("DH")
            ka.init(key, spec)
            return ka
        }
    }
}
