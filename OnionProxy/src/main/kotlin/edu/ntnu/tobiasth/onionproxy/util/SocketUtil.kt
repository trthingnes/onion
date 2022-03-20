package edu.ntnu.tobiasth.onionproxy.util

import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.net.Socket

class SocketUtil {
    companion object {
        fun getInput(socket: Socket): DataInputStream = DataInputStream(socket.getInputStream())
        fun getOutput(socket: Socket): BufferedOutputStream = BufferedOutputStream(socket.getOutputStream())
    }
}