package edu.ntnu.tobiasth.onionproxy

import java.io.DataInputStream
import java.io.PrintWriter

interface SocksProtocol {
    /**
     * Performs a SOCKS opening handshake with the InputStream and Writer provided.
     */
    fun performHandshake(input: DataInputStream, writer: PrintWriter)

    /**
     * Handles a SOCKS command with the InputStream and Writer provided.
     * Should only be used after a handshake to have taken place.
     */
    fun handleCommand(input: DataInputStream, writer: PrintWriter)
}