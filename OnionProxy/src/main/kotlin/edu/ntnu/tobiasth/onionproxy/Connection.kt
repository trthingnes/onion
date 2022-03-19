package edu.ntnu.tobiasth.onionproxy

import java.io.*
import java.net.Socket
import java.nio.CharBuffer
import java.lang.Appendable

class Connection(private var reader: BufferedReader, private var writer: PrintWriter): Appendable, AutoCloseable, Closeable, Flushable, Readable {
    constructor(socket: Socket): this(
        BufferedReader(InputStreamReader(socket.getInputStream())),
        PrintWriter(socket.getOutputStream(), true)
    )

    override fun close() {
        reader.close()
        writer.close()
    }

    override fun flush() {
        writer.flush()
    }

    override fun append(csq: CharSequence?): Appendable {
        return writer.append(csq)
    }

    override fun append(csq: CharSequence?, start: Int, end: Int): Appendable {
        return writer.append(csq, start, end)
    }

    override fun append(c: Char): Appendable {
        return writer.append(c)
    }

    override fun read(cb: CharBuffer): Int {
        return reader.read(cb)
    }

    fun readLine(): String {
        return reader.readLine()
    }
}