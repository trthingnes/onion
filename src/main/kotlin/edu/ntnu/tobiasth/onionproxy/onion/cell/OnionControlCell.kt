package edu.ntnu.tobiasth.onionproxy.onion.cell

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
) : OnionCell(circuitId, command, data)
