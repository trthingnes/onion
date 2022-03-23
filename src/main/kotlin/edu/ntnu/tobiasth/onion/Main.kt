package edu.ntnu.tobiasth.onion

import edu.ntnu.tobiasth.onion.onion.OnionProxy
import edu.ntnu.tobiasth.onion.onion.OnionRouter
import kotlin.concurrent.thread
import kotlin.system.exitProcess

/**
 * Runs onion routers on the configured ports, and a single proxy on the configured port.
 */
fun main() {
    val ports = mutableListOf<Int>()
    ports.addAll(Config.ONION_ROUTER_PORTS)
    ports.forEach {
        thread { OnionRouter(it) }
    }

    thread {
        OnionProxy()
    }
    println("Press enter to force stop.")
    readln()
    exitProcess(0)
}
