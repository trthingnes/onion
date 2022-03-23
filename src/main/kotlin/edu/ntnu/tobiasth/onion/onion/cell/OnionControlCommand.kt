package edu.ntnu.tobiasth.onion.onion.cell

/**
 * Commands used in control cells.
 */
enum class OnionControlCommand {
    /**
     * Relay data to next node in chain.
     */
    RELAY,

    /**
     * Begin an internal connection with this router.
     */
    CREATE,

    /**
     * End an internal connection with this router.
     */
    DESTROY
}
