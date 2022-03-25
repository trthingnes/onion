import edu.ntnu.tobiasth.onionrouting.util.DiffieHellmanUtil
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class DiffieHellmanUtilTest {
    @Test
    @DisplayName("Key exchange with two key pairs gives the same shared secret.")
    internal fun testSharedSecretIsCorrect() {
        val aliceKeyPair = DiffieHellmanUtil.getKeyPair()
        val bobKeyPair = DiffieHellmanUtil.getKeyPair()
        val params = DiffieHellmanUtil.getDHParameterSpec()

        val aliceSharedSecret = DiffieHellmanUtil.getSharedSecret(aliceKeyPair.private, bobKeyPair.public, params)
        val bobSharedSecret = DiffieHellmanUtil.getSharedSecret(bobKeyPair.private, aliceKeyPair.public, params)

        assertContentEquals(aliceSharedSecret, bobSharedSecret)
    }

    @Test
    @DisplayName("Public key is equal before and after encoding/decoding.")
    internal fun testEncodingDecodingPublicKey() {
        val originalKey = DiffieHellmanUtil.getKeyPair().public
        val encodedBytes = originalKey.encoded
        val decodedKey = DiffieHellmanUtil.getPublicKeyFromEncoded(encodedBytes)

        assertEquals(originalKey, decodedKey)
    }
}
