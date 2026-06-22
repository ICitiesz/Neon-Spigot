package com.islandstudio.neon.api.context

import kotlinx.coroutines.ThreadContextElement
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

class CoroutineUserContext(val userContext: UserContext): AbstractCoroutineContextElement(CoroutineContextKey), ThreadContextElement<UserContext?> {
    companion object CoroutineContextKey : CoroutineContext.Key<CoroutineUserContext>

    override fun updateThreadContext(context: CoroutineContext): UserContext? {
        val existingUserContext = SecurityContextHolder.getContext()

        SecurityContextHolder.setContext(userContext)
        return existingUserContext
    }

    override fun restoreThreadContext(context: CoroutineContext, oldState: UserContext?) {
        if (oldState == null) {
            SecurityContextHolder.releaseContext()
        } else {
            SecurityContextHolder.setContext(oldState)
        }
    }
    
}