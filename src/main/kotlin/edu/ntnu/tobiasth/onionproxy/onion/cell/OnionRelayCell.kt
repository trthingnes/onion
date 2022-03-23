package edu.ntnu.tobiasth.onionproxy.onion.cell

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
) : OnionCell(circuitId, OnionControlCommand.RELAY, data)
