package edu.ntnu.tobiasth.onionproxy.onion.cell

import java.io.Serializable
import java.util.*

open class OnionCell(val circuitId: UUID, val command: OnionControlCommand, val data: ByteArray): Serializable