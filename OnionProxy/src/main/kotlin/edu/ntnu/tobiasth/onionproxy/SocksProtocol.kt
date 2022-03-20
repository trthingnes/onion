package edu.ntnu.tobiasth.onionproxy

import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.net.Socket

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

    /**
     * Forwards content from client to remote and vice versa.
     * Should only be used after Connect command.
     */
    fun exchangeData(clientIn: DataInputStream, clientOut: BufferedOutputStream, remoteIn: DataInputStream, remoteOut: BufferedOutputStream)
}