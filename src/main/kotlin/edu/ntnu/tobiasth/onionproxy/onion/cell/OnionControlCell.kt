package edu.ntnu.tobiasth.onionproxy.onion.cell

import java.util.UUID

class OnionControlCell(
    circuitId: UUID,
    command: OnionControlCommand,
    data: ByteArray
) : OnionCell(circuitId, command, data)
