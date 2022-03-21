package edu.ntnu.tobiasth.onionproxy.onion.stream

import edu.ntnu.tobiasth.onionproxy.onion.OnionCircuit
import java.io.BufferedInputStream
import java.io.InputStream

class OnionInputStream(input: InputStream, val circuit: OnionCircuit) : BufferedInputStream(input) {
    override fun read(b: ByteArray): Int {
        TODO()
        // Call super.read(b) and take note of number of bytes.
        // Make a cell instance from the data and decrypt the layers on it.
        // Extract data from decrypted cell.
        // ! Make sure correct number of bytes is returned to caller.
    }
}