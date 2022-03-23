package edu.ntnu.tobiasth.onionproxy.onion

class OnionRouterDirectory(val routers: List<OnionRouterInfo> = listOf()) {
    fun getId(router: OnionRouterInfo): Int {
        return routers.indexOf(router)
    }

    fun getRouter(id: Int): OnionRouterInfo {
        return routers[id]
    }
}
