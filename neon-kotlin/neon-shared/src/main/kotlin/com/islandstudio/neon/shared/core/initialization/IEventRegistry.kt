package com.islandstudio.neon.shared.core.initialization

import com.islandstudio.neon.shared.PluginAdapter
import com.islandstudio.neon.shared.core.di.IComponentInjector
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.inject

interface IEventRegistry: IComponentInjector {
    fun registerEvent(event: Listener) {
        val pluginAdapter by inject<PluginAdapter<JavaPlugin>>()

        val isEventRegistered = HandlerList.getRegisteredListeners(pluginAdapter.plugin).any {
            it.listener.javaClass == event.javaClass
        }

        if (isEventRegistered) return

        pluginAdapter.plugin.server.pluginManager.registerEvents(event, pluginAdapter.plugin)
    }

    fun unregisterEvent(event: Listener) {
        val pluginAdapter by inject<PluginAdapter<JavaPlugin>>()

        HandlerList.getRegisteredListeners(pluginAdapter.plugin).find {
            it.listener.javaClass == event.javaClass
        }?.let { HandlerList.unregisterAll(it.listener) }
    }
}