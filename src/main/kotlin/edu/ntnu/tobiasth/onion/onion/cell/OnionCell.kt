package edu.ntnu.tobiasth.onion.onion.cell

import edu.ntnu.tobiasth.onion.onion.OnionCircuit
import java.io.Serializable
import java.util.*

/**
 * Data cell that can be sent through a circuit.
 * @see OnionCircuit
 * @see OnionControlCell
 * @see OnionRelayCell
 */
open class OnionCell(val circuitId: UUID, val command: OnionControlCommand, val data: ByteArray) : Serializable
