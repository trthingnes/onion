package edu.ntnu.tobiasth.onionproxy

import edu.ntnu.tobiasth.onionproxy.socks.request.SocksRequest
import edu.ntnu.tobiasth.onionproxy.socks.handshake.SocksHandshakeMethod
import edu.ntnu.tobiasth.onionproxy.socks.handshake.SocksHandshakeRequest
import edu.ntnu.tobiasth.onionproxy.socks.handshake.SocksHandshakeResponse
import edu.ntnu.tobiasth.onionproxy.socks.request.SocksCommand
import edu.ntnu.tobiasth.onionproxy.socks.request.SocksReply
import edu.ntnu.tobiasth.onionproxy.socks.request.SocksResponse
import edu.ntnu.tobiasth.onionproxy.util.SocketUtil
import mu.KotlinLogging
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.IOException
import java.net.Socket
import kotlin.concurrent.thread

class Socks5: SocksProtocol {
    private val logger = KotlinLogging.logger {}

    override fun performHandshake(input: DataInputStream, output: BufferedOutputStream) {
        logger.debug { "Reading handshake request from socket." }

        val request = SocksHandshakeRequest(input)
        val method = SocksHandshakeMethod.NO_AUTHENTICATION_REQUIRED
        logger.debug { "$method is the server method" }

        if (method !in request.methods) {
            throw IllegalArgumentException("Method $method is not in client handshake")
        }

        val response = SocksHandshakeResponse(method, request)
        logger.debug { "Writing handshake response to client." }
        response.toByteList().forEach { output.write(it) }
        output.flush()
    }

    override fun handleCommand(input: DataInputStream, output: BufferedOutputStream) {
        val request = SocksRequest(input)

        logger.debug { "Performing command ${request.command}." }
        when (request.command) {
            SocksCommand.CONNECT -> {
                logger.debug { "Creating socket to ${request.destAddress}:${request.destPort}." }
                val remoteSocket = Socket(request.destAddress, request.destPort)
                logger.debug { "Socket ${if (remoteSocket.isConnected) "connected." else "not connected."}" }
                val remoteInput = SocketUtil.getInput(remoteSocket)
                val remoteOutput = SocketUtil.getOutput(remoteSocket)

                val response = SocksResponse(SocksReply.SUCCEEDED, Config.SOCKS_PORT)
                response.toByteList().forEach { output.write(it) }
                output.flush()

                logger.debug { "Starting data exchange." }
                exchangeData(input, output, remoteInput, remoteOutput)
            }

            // TODO: SocksCommand.BIND {}
            // TODO: SocksCommand.UDP_ASSOCIATE {}

            else -> {
                val response = SocksResponse(SocksReply.COMMAND_NOT_SUPPORTED, Config.SOCKS_PORT)
                response.toByteList().forEach { output.write(it) }
                output.flush()
                throw IllegalStateException("Unsupported command in SOCKS request")
            }
        }
    }

    override fun exchangeData(clientIn: DataInputStream, clientOut: BufferedOutputStream, remoteIn: DataInputStream, remoteOut: BufferedOutputStream) {
        val thread = thread { // Start separate thread so we have two.
            val buffer = ByteArray(4096)

            try {
                var byteCount = clientIn.read(buffer)
                while(byteCount != -1) {
                    logger.debug { "Read $byteCount bytes from client." }
                    remoteOut.write(buffer, 0, byteCount)
                    logger.debug { "Wrote $byteCount to remote." }
                    remoteOut.flush()
                    byteCount = clientIn.read(buffer)
                }
            }
            catch(e: IOException) {
                logger.debug { "Connection from client to server closed." }
                clientIn.close()
                remoteOut.close()
            }
        }

        val buffer = ByteArray(4096)

        try {
            var byteCount = remoteIn.read(buffer)
            while(byteCount != -1) {
                logger.debug { "Read $byteCount bytes from remote." }
                clientOut.write(buffer, 0, byteCount)
                logger.debug { "Wrote $byteCount to client." }
                clientOut.flush()
                byteCount = remoteIn.read(buffer)
            }
        }
        catch(e: IOException) {
            logger.debug { "Connection from server to client closed." }
            remoteIn.close()
            clientOut.close()
        }

        thread.join()
    }
}