package edu.ntnu.tobiasth.onionproxy.onion.cell

import java.io.Serializable

open class OnionCell(val circuitId: Int, val command: OnionControlCommand, val data: ByteArray): Serializable {
    /**
     * Serializes and encrypts the current cell and adds the result as data to a new cell which is returned.
     * Cells can only be wrapped in relay cells since control cells are the lowest level.
     */
    fun wrap(sharedSecret: ByteArray, command: OnionRelayCommand): OnionRelayCell {
        TODO()
    }
}