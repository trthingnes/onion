import edu.ntnu.tobiasth.onionrouting.util.EncryptionUtil
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.security.SecureRandom
import kotlin.test.assertContentEquals

class EncryptionUtilTest {
    @Test
    @DisplayName("Encryption and decryption with equal spec succeeds.")
    internal fun testEncryptionDecryption() {
        val sharedSecret = ByteArray(32)
        SecureRandom().nextBytes(sharedSecret)

        val aliceCipher = EncryptionUtil.getEncryptCipher(sharedSecret)
        val bobCipher = EncryptionUtil.getDecryptCipher(sharedSecret)

        val originalData = "This is a test string :)".toByteArray()
        val encryptedData = EncryptionUtil.useCipher(aliceCipher, originalData)
        val decryptedData = EncryptionUtil.useCipher(bobCipher, encryptedData)

        assertContentEquals(originalData, decryptedData)
    }
}
