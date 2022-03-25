package edu.ntnu.tobiasth.onionrouting.onion.cell

/**
 * Commands used in relay cells.
 */
enum class OnionRelayCommand {
    /**
     * Relay data to next node in chain.
     */
    RELAY,

    /**
     * Relay data to an external connection.
     */
    DATA,

    /**
     * Begin new external connection.
     */
    BEGIN,

    /**
     * End an external connection.
     */
    END,

    /**
     * Begin an internal connection with the given router.
     * The router that receives this will send a create command to the given router.
     * @see OnionControlCommand.CREATE
     */
    EXTEND,

    /**
     * End an internal connection to the next router in the chain.
     * The router that receives this will send a destroy command to the next router.
     * @see OnionControlCommand.DESTROY
     */
    TRUNCATE
}
