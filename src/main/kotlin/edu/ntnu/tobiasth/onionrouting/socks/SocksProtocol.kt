package edu.ntnu.tobiasth.onionrouting.socks

import java.io.InputStream
import java.io.OutputStream

/**
 * Interface describing the requirements for a SOCKS implementation.
 */
interface SocksProtocol {
    /**
     * Handles a SOCKS opening handshake with the streams provided.
     * @param input Client input stream.
     * @param output Client output stream.
     */
    fun handleHandshake(input: InputStream, output: OutputStream)

    /**
     * Handles a SOCKS command with the streams provided.
     * Should only be used after a handshake to have taken place.
     * @param input Client input stream.
     * @param output Client output stream.
     */
    fun handleCommand(input: InputStream, output: OutputStream)

    /**
     * Forwards content from client to remote and vice versa.
     * Should only be used after connect command.
     * @param clientIn Client input stream.
     * @param clientOut Client output stream.
     * @param remoteIn Remote input stream.
     * @param remoteOut Remote output stream.
     */
    fun exchangeData(clientIn: InputStream, clientOut: OutputStream, remoteIn: InputStream, remoteOut: OutputStream)
}
