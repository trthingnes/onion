package edu.ntnu.tobiasth.onionproxy.onion.cell

class OnionControlCell(circuitId: Int, command: OnionControlCommand, data: ByteArray) : OnionCell(circuitId, command, data)