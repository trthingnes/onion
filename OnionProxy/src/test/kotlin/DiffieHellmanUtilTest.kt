import edu.ntnu.tobiasth.onionproxy.util.DiffieHellmanUtil
import org.junit.jupiter.api.Test
import javax.crypto.interfaces.DHPrivateKey
import kotlin.test.assertContentEquals

internal class DiffieHellmanUtilTest {
    @Test
    fun testSharedSecretIsCorrect() {
        val aliceKeyPair = DiffieHellmanUtil.getKeyPair()
        val bobKeyPair = DiffieHellmanUtil.getKeyPair()
        val params = (aliceKeyPair.public as DHPrivateKey).params

        val aliceSharedSecret = DiffieHellmanUtil.getSharedSecret(aliceKeyPair.private, bobKeyPair.public, params)
        val bobSharedSecret = DiffieHellmanUtil.getSharedSecret(bobKeyPair.private, aliceKeyPair.public, params)

        assertContentEquals(aliceSharedSecret, bobSharedSecret)
    }
}