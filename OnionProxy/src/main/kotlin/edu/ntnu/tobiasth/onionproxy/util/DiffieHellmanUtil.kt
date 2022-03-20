package edu.ntnu.tobiasth.onionproxy.util

import java.security.*
import java.security.spec.X509EncodedKeySpec
import javax.crypto.KeyAgreement
import javax.crypto.interfaces.DHPrivateKey
import javax.crypto.interfaces.DHPublicKey
import javax.crypto.spec.DHParameterSpec

class DiffieHellmanUtil {
    companion object {
        fun getSharedSecret(privateKey: PrivateKey, publicKey: PublicKey, spec: DHParameterSpec): ByteArray {
            val keyAgreement = getKeyAgreement(privateKey, spec)
            keyAgreement.doPhase(publicKey, true)
            return keyAgreement.generateSecret()
        }

        fun getPublicKeyFromEncoded(bytes: ByteArray): DHPublicKey {
            val keyFactory = KeyFactory.getInstance("DH")
            return keyFactory.generatePublic(X509EncodedKeySpec(bytes)) as DHPublicKey
        }

        fun getPrivateKeyFromEncoded(bytes: ByteArray): DHPrivateKey {
            val keyFactory = KeyFactory.getInstance("DH")
            return keyFactory.generatePrivate(X509EncodedKeySpec(bytes)) as DHPrivateKey
        }

        fun getKeyPair(): KeyPair {
            val keyPairGenerator = KeyPairGenerator.getInstance("DH")
            return keyPairGenerator.genKeyPair()
        }

        private fun getKeyAgreement(key: PrivateKey, spec: DHParameterSpec): KeyAgreement {
            val ka = KeyAgreement.getInstance("DH")
            ka.init(key, spec)
            return ka
        }
    }
}