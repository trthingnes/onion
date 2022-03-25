package edu.ntnu.tobiasth.onion.onion.cell

import java.util.*

/**
 * Data cell that tells a router to relay a cell to another router.
 * @see OnionRelayCommand
 * @see OnionCell
 */
class OnionRelayCell(
    circuitId: UUID,
    val relayCommand: OnionRelayCommand,
    data: ByteArray,
) : OnionCell(circuitId, OnionControlCommand.RELAY, data) {
    override fun equals(other: Any?): Boolean {
        if (other !is OnionRelayCell) {
            return false
        }

        return circuitId == other.circuitId && relayCommand == other.relayCommand && data.contentEquals(other.data)
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}
