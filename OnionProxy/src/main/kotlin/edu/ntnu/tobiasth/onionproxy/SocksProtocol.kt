package edu.ntnu.tobiasth.onionproxy

import java.io.BufferedOutputStream
import java.io.DataInputStream

interface SocksProtocol {
    /**
     * Performs a SOCKS opening handshake with the InputStream and Writer provided.
     */
    fun performHandshake(input: DataInputStream, output: BufferedOutputStream)

    /**
     * Handles a SOCKS command with the InputStream and Writer provided.
     * Should only be used after a handshake to have taken place.
     */
    fun handleCommand(input: DataInputStream, output: BufferedOutputStream)
}