package edu.ntnu.tobiasth.onion.onion.cell

import java.util.*

/**
 * Data cell that tells a router that receives it to do something.
 * @see OnionControlCommand
 * @see OnionCell
 */
class OnionControlCell(
    circuitId: UUID,
    command: OnionControlCommand,
    data: ByteArray
) : OnionCell(circuitId, command, data) {
    override fun equals(other: Any?): Boolean {
        if (other !is OnionControlCell) {
            return false
        }

        return circuitId == other.circuitId && command == other.command && data.contentEquals(other.data)
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}
