package edu.ntnu.tobiasth.onionproxy.onion.cell

class OnionRelayCell(
    circuitId: Int,
    command: OnionControlCommand,
    data: ByteArray,
    val streamId: Int,
    val length: Int,
    val relayCommand: OnionRelayCommand
): OnionCell(circuitId, command, data) {
    /**
     * Decrypts and deserializes the data in this cell and returns the wrapped cell.
     * After unwrapping, one should always check what kind of cell is contained.
     */
    fun unwrap(sharedSecret: ByteArray): OnionCell {
        if(relayCommand == OnionRelayCommand.DATA) {
            throw IllegalStateException("Cannot unwrap a data cell.")
        }

        TODO()
        // * Encryption fails is the data is not encrypted.
        // This can potentially be used to find out when data is decrypted.
    }
}