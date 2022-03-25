import edu.ntnu.tobiasth.onion.Config
import edu.ntnu.tobiasth.onion.onion.OnionRouter
import edu.ntnu.tobiasth.onion.onion.cell.OnionControlCell
import edu.ntnu.tobiasth.onion.onion.cell.OnionControlCommand
import edu.ntnu.tobiasth.onion.onion.cell.OnionRelayCell
import edu.ntnu.tobiasth.onion.onion.cell.OnionRelayCommand
import edu.ntnu.tobiasth.onion.util.OnionUtil
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.security.SecureRandom
import java.util.*
import kotlin.concurrent.thread
import kotlin.test.assertEquals

class OnionUtilTest {
    @Test
    @DisplayName("Control cell is the same before and after serialization.")
    internal fun testControlCellIsSameAfterSerialization() {
        val originalCell = OnionControlCell(UUID.randomUUID(), OnionControlCommand.CREATE, byteArrayOf(0, 1, 2, 3))
        val serialized = OnionUtil.serializeCell(originalCell)
        val deserializedCell = OnionUtil.deserializeCell(serialized)

        assertEquals(originalCell, deserializedCell)
    }

    @Test
    @DisplayName("Relay cell is the same before and after serialization.")
    internal fun testRelayCellIsSameAfterSerialization() {
        val originalCell = OnionRelayCell(UUID.randomUUID(), OnionRelayCommand.RELAY, byteArrayOf(0, 1, 2, 3))
        val serialized = OnionUtil.serializeCell(originalCell)
        val deserializedCell = OnionUtil.deserializeCell(serialized)

        assertEquals(originalCell, deserializedCell)
    }

    @Test
    @DisplayName("Control cell is the same before and after encryption.")
    internal fun testControlCellIsSameAfterEncryption() {
        val sharedSecret = ByteArray(32)
        SecureRandom().nextBytes(sharedSecret)

        val originalCell = OnionControlCell(UUID.randomUUID(), OnionControlCommand.CREATE, byteArrayOf(0, 1, 2, 3))
        val encrypted = OnionUtil.encryptCell(originalCell, sharedSecret)
        val decryptedCell = OnionUtil.decryptCell(encrypted, sharedSecret)

        assertEquals(originalCell, decryptedCell)
    }

    @Test
    @DisplayName("Relay cell is the same before and after encryption.")
    internal fun testRelayCellIsSameAfterEncryption() {
        val sharedSecret = ByteArray(32)
        SecureRandom().nextBytes(sharedSecret)

        val originalCell = OnionRelayCell(UUID.randomUUID(), OnionRelayCommand.RELAY, byteArrayOf(0, 1, 2, 3))
        val encrypted = OnionUtil.encryptCell(originalCell, sharedSecret)
        val decryptedCell = OnionUtil.decryptCell(encrypted, sharedSecret)

        assertEquals(originalCell, decryptedCell)
    }

    @Test
    @DisplayName("Circuit with the configured number of routers can be built.")
    internal fun testCircuitCanBeBuilt() {
        val ports = mutableListOf<Int>()
        ports.addAll(Config.ONION_ROUTER_PORTS)
        ports.forEach {
            thread { OnionRouter(it) }
        }

        assertDoesNotThrow {
            OnionUtil.createCircuit(Config.ONION_CIRCUIT_SIZE, Config.ONION_ROUTER_DIRECTORY)
        }
    }
}
