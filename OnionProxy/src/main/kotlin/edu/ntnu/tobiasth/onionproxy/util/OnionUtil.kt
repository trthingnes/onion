package edu.ntnu.tobiasth.onionproxy.util

import edu.ntnu.tobiasth.onionproxy.onion.OnionCircuit
import edu.ntnu.tobiasth.onionproxy.onion.OnionRouterDirectory
import edu.ntnu.tobiasth.onionproxy.onion.OnionRouterInfo

class OnionUtil {
    companion object {
        private var id = 0

        fun createCircuit(size: Int, directory: OnionRouterDirectory): OnionCircuit {
            val routers = arrayListOf<OnionRouterInfo>()
            routers.addAll(directory.routers)

            val firstRouter = routers.random()
            routers.remove(firstRouter)
            val circuit = OnionCircuit(id++, firstRouter)

            try {
                repeat(size - 1) {
                    val randomRouter = routers.random()
                    routers.remove(randomRouter)
                    circuit.extend(randomRouter)
                }
            }
            catch(e: NoSuchElementException) {
                throw IllegalStateException("Not enough routers to create circuit of size $size")
            }

            return circuit
        }
    }
}