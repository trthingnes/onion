package edu.ntnu.tobiasth.onionrouting.onion.stream

import edu.ntnu.tobiasth.onionrouting.onion.OnionCircuit
import edu.ntnu.tobiasth.onionrouting.onion.cell.OnionRelayCell
import edu.ntnu.tobiasth.onionrouting.onion.cell.OnionRelayCommand
import java.io.InputStream

/**
 * Input stream to receive data through a circuit.
 * @see OnionCircuit
 * @see InputStream
 */
class OnionInputStream(private val circuit: OnionCircuit) : InputStream() {
    override fun read(b: ByteArray): Int {
        val cell = circuit.receive()

        if (cell !is OnionRelayCell || cell.relayCommand != OnionRelayCommand.DATA) {
            throw IllegalStateException("Unexpected response cell")
        }

        val data = cell.data
        for (i in data.indices) {
            b[i] = data[i]
        }

        return data.size
    }

    override fun read(): Int {
        throw NotImplementedError("Not implemented")
    }
}
