package edu.ntnu.tobiasth.onionproxy.socks

import edu.ntnu.tobiasth.onionproxy.Config
import edu.ntnu.tobiasth.onionproxy.Request
import mu.KotlinLogging
import java.io.Reader
import java.net.InetAddress

class SocksRequest(reader: Reader): Request {
    var command: SocksCommand
    var addressType: SocksAddressType
    var destAddress: InetAddress
    var destPort: Int

    init {
        // Make sure the SOCKS version given is correct.
        if (reader.read().compareTo(Config.SOCKS_VERSION) != 0) {
            throw IllegalArgumentException("Invalid SOCKS version.")
        }

        command = SocksCommand.of(reader.read())
        addressType = SocksAddressType.of(reader.read())

        when(addressType) {
            SocksAddressType.IPV4 -> {
                val bytes = ByteArray(4)
                repeat(4) {
                    bytes[it] = reader.read().toByte()
                }

                destAddress = InetAddress.getByAddress(bytes)
                destPort = (reader.read() shl(8)) or reader.read()
            }

            SocksAddressType.IPV6 -> {
                val bytes = ByteArray(6)
                repeat(6) {
                    bytes[it] = reader.read().toByte()
                }

                destAddress = InetAddress.getByAddress(bytes)
                destPort = (reader.read() shl(8)) or reader.read()
            }

            SocksAddressType.DOMAIN_NAME -> {
                val length = reader.read()
                val bytes = ByteArray(length)
                repeat(length) {
                    bytes[it] = reader.read().toByte()
                }

                destAddress = InetAddress.getByName(String(bytes))
                destPort = (reader.read() shl(8)) or reader.read()
            }
        }
    }
}