package edu.ntnu.tobiasth.onion.onion

/**
 * Directory of routers that a circuit can be built from.
 */
class OnionRouterDirectory(val routers: List<OnionRouterInfo> = listOf()) {
    /**
     * Gets a unique id for a router.
     * @param router Router to get id from.
     * @return Router id.
     */
    fun getId(router: OnionRouterInfo): Int {
        return routers.indexOf(router)
    }

    /**
     * Gets the router with the given id.
     * @param id Router id.
     * @return Router with given id.
     */
    fun getRouter(id: Int): OnionRouterInfo {
        return routers[id]
    }
}
