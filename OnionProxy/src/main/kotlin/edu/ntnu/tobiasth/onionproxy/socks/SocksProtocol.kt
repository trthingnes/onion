package edu.ntnu.tobiasth.onionproxy.socks

import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.InputStream
import java.io.OutputStream

interface SocksProtocol {
    /**
     * Handles a SOCKS opening handshake with the streams provided.
     */
    fun handleHandshake(input: DataInputStream, output: BufferedOutputStream)

    /**
     * Handles a SOCKS command with the streams provided.
     * Should only be used after a handshake to have taken place.
     */
    fun handleCommand(input: DataInputStream, output: BufferedOutputStream)

    /**
     * Forwards content from client to remote and vice versa.
     * Should only be used after Connect command.
     */
    fun exchangeData(clientIn: InputStream, clientOut: OutputStream, remoteIn: InputStream, remoteOut: OutputStream)
}