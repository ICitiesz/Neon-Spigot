package com.islandstudio.neon.shared.rework.core.initialization.runner

import com.islandstudio.neon.shared.core.initialization.IEventRegistryNew
import org.bukkit.event.Listener

interface IRunnerAsync: IEventRegistryNew {
    suspend fun runSuspend() {}

    fun eventRegistrationOnToggle(toggleStatus: Boolean, event: Listener, onToggleOn: () -> Unit = {}, onToggleOff: () -> Unit = {}) {
        if (toggleStatus) {
            onToggleOn()
            registerEvent(event)
        } else {
            onToggleOff()
            unregisterEvent(event)
        }
    }
}