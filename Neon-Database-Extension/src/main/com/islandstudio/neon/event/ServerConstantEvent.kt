package com.islandstudio.neon.event

import com.islandstudio.neon.DatabaseController
import com.islandstudio.neon.NeonDatabaseExtension
import com.islandstudio.neon.application.AppContext
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.server.ServerCommandEvent
import org.jetbrains.kotlin.utils.addToStdlib.ifFalse
import org.koin.core.component.inject

class ServerConstantEvent: Listener {
    private val reloadCommands = arrayListOf("rl", "reload", "bukkit:reload", "bukkit:rl")
    private val reloadCommandsWithConfirm = arrayListOf("rl confirm", "reload confirm", "bukkit:reload confirm", "bukkit:rl confirm")
    private val serverName = dbExtension.server.name
    private val doLetMeReload: Boolean? = with(System.getProperties()) {
        return@with this.entries.find { it.key == "LetMeReload" }?.let {
            (it.value as String).toBoolean()
        }
    }

    companion object: AppContext.Injector {
        private val dbExtension by inject<NeonDatabaseExtension>()

        fun registerEvent() {
            HandlerList.getRegisteredListeners(dbExtension).find {
                it.listener.javaClass.canonicalName == it.listener.javaClass.canonicalName
            }?.let { return }

            dbExtension.server.pluginManager.registerEvents(EventProcessor(), dbExtension)
        }

        fun unregisterEvent() {
            HandlerList.getRegisteredListeners(dbExtension).find {
                it.listener.javaClass.canonicalName == it.listener.javaClass.canonicalName
            }?.let { HandlerList.unregisterAll(it.listener) }
        }
    }

    /**
     * Perform database server shutdown based on command processing event.
     *
     * @param e Either [ServerCommandEvent] or [PlayerCommandPreprocessEvent]
     */
    private fun performDbShutdown(e: Event) {
        val command: String = when(e) {
            is ServerCommandEvent -> {
                e.command.lowercase()
            }

            is PlayerCommandPreprocessEvent -> {
                if (!e.player.isOp) return

                e.message.lowercase().replace("/", "")
            }

            else -> return
        }

        (command in reloadCommands || command in reloadCommandsWithConfirm || command.equals("stop", true)).ifFalse {
            return
        }

        with(serverName) {
            when {
                this.equals("CraftBukkit", true) -> return@with

                this.equals("Paper", true) -> {
                    if (command.equals("stop", true)) return@with

                    if (doLetMeReload == null || doLetMeReload == false) {
                        if (command in reloadCommandsWithConfirm) return@with

                        return
                    }

                    return@with
                }
            }
        }

        DatabaseController.stopDbServer()
    }

    private class EventProcessor: Listener {
        private val serverConstantEvent = ServerConstantEvent()

        @EventHandler
        private fun onServerCommandSend(e: ServerCommandEvent) {
            serverConstantEvent.performDbShutdown(e)
        }

        @EventHandler
        private fun onPlayerCommandPreprocess(e: PlayerCommandPreprocessEvent) {
            serverConstantEvent.performDbShutdown(e)
        }
    }


}