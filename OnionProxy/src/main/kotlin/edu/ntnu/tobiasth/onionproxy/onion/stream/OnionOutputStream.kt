package edu.ntnu.tobiasth.onionproxy.onion.stream

import edu.ntnu.tobiasth.onionproxy.onion.OnionCircuit
import edu.ntnu.tobiasth.onionproxy.onion.cell.OnionCell
import edu.ntnu.tobiasth.onionproxy.onion.cell.OnionControlCommand
import java.io.BufferedOutputStream
import java.io.OutputStream

class OnionOutputStream(output: OutputStream, private val circuit: OnionCircuit) : BufferedOutputStream(output) {
    override fun write(b: ByteArray, off: Int, len: Int) {
        // Put data into cell.
        val cell = OnionCell(circuit.id, OnionControlCommand.RELAY, b)

        // Encrypt all layers onto cell.
        // Call super.write(b, off, len)
        // ! Make sure len is updated to match length of encrypted data.
    }

    override fun close() {
        // TODO: Possibly add support for more then one stream using the same circuit.
        circuit.destroy()
        super.close()
    }
}