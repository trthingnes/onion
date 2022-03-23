package edu.ntnu.tobiasth.onionproxy

import kotlin.concurrent.thread
import kotlin.system.exitProcess

fun main() {
    thread {
        OnionRouter(1111)
    }
    thread {
        OnionRouter(2222)
    }
    thread {
        OnionRouter(3333)
    }
    println("Press enter to force stop.")
    readln()
    exitProcess(1)
}
