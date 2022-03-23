package edu.ntnu.tobiasth.onion.onion.stream

import edu.ntnu.tobiasth.onion.onion.OnionCircuit
import edu.ntnu.tobiasth.onion.onion.cell.OnionRelayCell
import edu.ntnu.tobiasth.onion.onion.cell.OnionRelayCommand
import java.io.OutputStream

/**
 * Output stream to send data through a circuit.
 * @see OnionCircuit
 * @see OutputStream
 */
class OnionOutputStream(private val circuit: OnionCircuit) : OutputStream() {
    override fun write(b: ByteArray, off: Int, len: Int) {
        val data = ByteArray(len) { b[off + it] }
        val cell = OnionRelayCell(circuit.id, OnionRelayCommand.DATA, data)
        circuit.send(cell)
    }

    override fun write(b: Int) {
        throw NotImplementedError("Not implemented")
    }
}