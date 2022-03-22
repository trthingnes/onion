package edu.ntnu.tobiasth.onionproxy.onion.stream

import edu.ntnu.tobiasth.onionproxy.onion.OnionCircuit
import java.io.InputStream

class OnionInputStream(private val circuit: OnionCircuit): InputStream() {
    override fun read(b: ByteArray): Int {
        val cell = circuit.receive()

        val data = cell.data
        for (i in data.indices) {
            b[i] = data[i]
        }

        return data.size
    }

    override fun read(): Int {
        TODO("Not implemented")
    }
}