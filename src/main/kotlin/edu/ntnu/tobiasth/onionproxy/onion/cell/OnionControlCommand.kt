package edu.ntnu.tobiasth.onionproxy.onion.cell

/**
 * Commands used in control cells.
 * These are used when one router is communicating to another.
 */
enum class OnionControlCommand {
    /**
     * Relay data to next node in chain.
     */
    RELAY,

    /**
     * Begin an internal connection.
     */
    CREATE,

    /**
     * End an internal connection.
     */
    DESTROY
}
