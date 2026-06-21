package com.islandstudio.neon.util

import com.islandstudio.neon.shared.rework.core.di.IComponentProvider
import com.islandstudio.neon.shared.rework.core.di.getComponent
import com.islandstudio.neon.shared.rework.core.initialization.context.IPluginContext
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.server.ServerCommandEvent

object ServerUtil: IComponentProvider {
    private val pluginContext = getComponent<IPluginContext>()
    private val serverName = pluginContext.getServer().name
    private val reloadCommands = arrayListOf("rl", "reload", "bukkit:reload", "bukkit:rl")
    private val reloadCommandsWithConfirm = arrayListOf("rl confirm", "reload confirm", "bukkit:reload confirm", "bukkit:rl confirm")
    private val doLetMeReload: Boolean? = with(System.getProperties()) {
        return@with this.entries.find { it.key == "LetMeReload" }?.let {
            (it.value as String).toBoolean()
        }
    }

    internal inline fun <T> onServerReload(e: Event, onServerReloadFunc: () -> T) {
        val command = when(e) {
            is ServerCommandEvent -> {
                e.command.lowercase()
            }

            is PlayerCommandPreprocessEvent -> {
                if (!e.player.isOp) return

                e.message.lowercase().replace("/", "")
            }

            else -> return
        }

        if (!(command in reloadCommands || command in reloadCommandsWithConfirm)) {
            return
        }

        val isMatchServerName = when {
            serverName.equals("Bukkit", true) -> true

            serverName.equals("Paper", true) -> {
                doLetMeReload == null || !doLetMeReload
            }

            else -> false
        }

        if (!isMatchServerName) return

        onServerReloadFunc()
    }
}