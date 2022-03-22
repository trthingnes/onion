package edu.ntnu.tobiasth.onionproxy.onion.cell

import java.util.*

class OnionRelayCell(
    circuitId: UUID,
    val relayCommand: OnionRelayCommand,
    data: ByteArray,
): OnionCell(circuitId, OnionControlCommand.RELAY, data)