package edu.ntnu.tobiasth.onionproxy.onion.cell

import java.util.*

class OnionRelayCell(
    circuitId: UUID,
    data: ByteArray,
    val streamId: UUID,
    val relayCommand: OnionRelayCommand,
): OnionCell(circuitId, OnionControlCommand.RELAY, data)