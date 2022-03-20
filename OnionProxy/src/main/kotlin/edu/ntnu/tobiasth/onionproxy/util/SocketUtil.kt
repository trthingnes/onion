package edu.ntnu.tobiasth.onionproxy.util

import edu.ntnu.tobiasth.onionproxy.onion.OnionInputStream
import edu.ntnu.tobiasth.onionproxy.onion.OnionOutputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.net.Socket

class SocketUtil {
    companion object {
        fun getDirectInput(socket: Socket): DataInputStream = DataInputStream(socket.getInputStream())
        fun getDirectOutput(socket: Socket): BufferedOutputStream = BufferedOutputStream(socket.getOutputStream())
        //fun getOnionInput(socket: Socket): OnionInputStream = OnionInputStream(socket.getInputStream())
        //fun getOnionOutput(socket: Socket): OnionOutputStream = OnionOutputStream(socket.getOutputStream())
    }
}