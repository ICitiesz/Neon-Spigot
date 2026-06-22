package com.islandstudio.neon.api.context

object SecurityContextHolder {
    private val contextHolder = ThreadLocal<UserContext>()

    fun setContext(context: UserContext) {
        contextHolder.set(context)
    }

    fun getContext(): UserContext? {
        return contextHolder.get()
    }

    fun releaseContext() {
        contextHolder.remove()
    }
}