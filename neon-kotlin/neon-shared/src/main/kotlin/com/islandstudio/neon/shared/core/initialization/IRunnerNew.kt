package com.islandstudio.neon.shared.core.initialization

import org.bukkit.event.Listener

interface IRunnerNew: IEventRegistryNew {
    fun run() {}

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