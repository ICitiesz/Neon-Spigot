package com.islandstudio.neon.shared.core.initialization

import com.islandstudio.neon.shared.core.di.IComponentProvider
import com.islandstudio.neon.shared.core.di.getComponent
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener

interface IEventRegistryNew: IComponentProvider {
    fun registerEvent(event: Listener) {
        val pluginContext = getComponent<IPluginContext>()

        val isEventRegistered = HandlerList.getRegisteredListeners(pluginContext.mainPluginInstance).any {
            it.listener.javaClass == event.javaClass
        }

        if (isEventRegistered) return

        pluginContext.getServer().pluginManager.registerEvents(event, pluginContext.mainPluginInstance)
    }

    fun unregisterEvent(event: Listener) {
        val pluginContext = getComponent<IPluginContext>()

        HandlerList.getRegisteredListeners(pluginContext.mainPluginInstance).find {
            it.listener.javaClass == event.javaClass
        }?.let { HandlerList.unregisterAll(it.listener) }
    }
}