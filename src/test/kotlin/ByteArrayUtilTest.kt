
import edu.ntnu.tobiasth.onion.util.ByteArrayUtil
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals

class ByteArrayUtilTest {
    @Test
    @DisplayName("Array is correct after adding byte to front.")
    internal fun testByteCanBeAddedToArrayFront() {
        val original = byteArrayOf(1, 2, 3, 4, 5)
        val expected = byteArrayOf(0, 1, 2, 3, 4, 5)

        assertContentEquals(expected, ByteArrayUtil.addByteToFront(original, 0))
    }

    @Test
    @DisplayName("Array is correct after removing byte from front.")
    internal fun testByteCanBeRemovedFromArrayFront() {
        val original = byteArrayOf(0, 1, 2, 3, 4, 5)
        val expected = byteArrayOf(1, 2, 3, 4, 5)

        assertContentEquals(expected, ByteArrayUtil.removeByteFromFront(original))
    }
}
