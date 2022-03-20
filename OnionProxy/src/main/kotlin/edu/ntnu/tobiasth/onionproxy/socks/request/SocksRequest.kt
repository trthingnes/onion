package edu.ntnu.tobiasth.onionproxy.socks.request

import edu.ntnu.tobiasth.onionproxy.Config
import edu.ntnu.tobiasth.onionproxy.Request
import mu.KotlinLogging
import java.io.InputStream
import java.net.InetAddress

class SocksRequest(input: InputStream): Request {
    private val logger = KotlinLogging.logger {}
    var command: SocksCommand
    var addressType: SocksAddressType
    var destAddress: InetAddress
    var destPort: Int

    init {
        logger.debug { "Parsing request from client." }

        // Make sure the SOCKS version given is correct.
        if (input.read().compareTo(Config.SOCKS_VERSION) != 0) {
            throw IllegalArgumentException("Invalid SOCKS version")
        }

        try {
            command = SocksCommand.of(input.read())
            input.read() // Throw away reserved byte.
            addressType = SocksAddressType.of(input.read())
        }
        catch(e: Exception) {
            throw IllegalArgumentException("Unknown command and address type")
        }

        logger.debug { "Request has command $command and address type $addressType." }
        when(addressType) {
            SocksAddressType.IPV4 -> {
                val bytes = ByteArray(4)
                repeat(4) {
                    bytes[it] = input.read().toByte()
                }

                destAddress = InetAddress.getByAddress(bytes)
            }

            SocksAddressType.IPV6 -> {
                val bytes = ByteArray(16)
                repeat(16) {
                    bytes[it] = input.read().toByte()
                }

                destAddress = InetAddress.getByAddress(bytes)
            }

            SocksAddressType.DOMAIN_NAME -> {
                val length = input.read()
                val bytes = ByteArray(length)
                repeat(length) {
                    bytes[it] = input.read().toByte()
                }

                destAddress = InetAddress.getByName(String(bytes))
            }
        }

        destPort = (input.read() shl(8)) or input.read()
        logger.debug { "Request address is $destAddress:$destPort." }
    }
}