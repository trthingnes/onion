import edu.ntnu.tobiasth.onionproxy.util.EncryptionUtil
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.security.SecureRandom
import kotlin.test.assertContentEquals

internal class EncryptionUtilTest {
    @Test
    @DisplayName("Encryption and decryption with equal spec succeeds.")
    fun testEncryptionDecryption() {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)

        val aliceCipher = EncryptionUtil.getEncryptCipher(bytes)
        val bobCipher = EncryptionUtil.getDecryptCipher(bytes)

        val originalData = "This is a test string :)".toByteArray()
        val encryptedData = EncryptionUtil.useCipher(aliceCipher, originalData)
        val decryptedData = EncryptionUtil.useCipher(bobCipher, encryptedData)

        assertContentEquals(originalData, decryptedData)
    }
}