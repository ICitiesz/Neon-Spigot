package com.islandstudio.neon.shared.server

import com.islandstudio.neon.shared.core.AppContext
import com.islandstudio.neon.shared.core.di.IComponentInjector
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.server.ServerCommandEvent
import org.koin.core.component.inject

object ServerManager: IComponentInjector {
    private val appContext by inject<AppContext>()
    private val neonPlugin = appContext.getParentPlugin()

    private val serverName = neonPlugin.server.name
    private val reloadCommands = arrayListOf("rl", "reload", "bukkit:reload", "bukkit:rl")
    private val reloadCommandsWithConfirm = arrayListOf("rl confirm", "reload confirm", "bukkit:reload confirm", "bukkit:rl confirm")
    private val doLetMeReload: Boolean? = with(System.getProperties()) {
        return@with this.entries.find { it.key == "LetMeReload" }?.let {
            (it.value as String).toBoolean()
        }
    }

    fun onServerReload(e: Event, onServerReloadFunc: () -> Unit) {
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

    fun onServerClose(e: Event, onServerCloseFunc: () -> Unit) {
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

        if (!command.equals("stop", true)) return

        onServerCloseFunc()
    }
}