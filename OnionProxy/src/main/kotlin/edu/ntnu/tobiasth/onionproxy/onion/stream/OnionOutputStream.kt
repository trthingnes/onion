package edu.ntnu.tobiasth.onionproxy.onion.stream

import edu.ntnu.tobiasth.onionproxy.onion.OnionCircuit
import edu.ntnu.tobiasth.onionproxy.onion.cell.*
import java.io.OutputStream

class OnionOutputStream(private val circuit: OnionCircuit) : OutputStream() {
    override fun write(b: ByteArray, off: Int, len: Int) {
        val data = ByteArray(len) { b[off + it] }
        val cell = OnionRelayCell(circuit.id, OnionRelayCommand.DATA, data)
        circuit.send(cell)
    }

    override fun write(b: Int) {
        TODO("Not implemented")
    }
}