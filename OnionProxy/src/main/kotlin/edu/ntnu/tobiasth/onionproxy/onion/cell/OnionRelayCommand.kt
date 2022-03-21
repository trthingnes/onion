package edu.ntnu.tobiasth.onionproxy.onion.cell

/**
 * Commands used in relay cells.
 * These are used when forwarding command cells or data/actions for externals.
 */
enum class OnionRelayCommand {
    /**
     * Relay data to next node in chain.
     */
    RELAY,

    /**
     * Data for an external connection.
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
     * Begin an internal connection.
     */
    EXTEND,

    /**
     * End an internal connection.
     */
    TRUNCATE
}