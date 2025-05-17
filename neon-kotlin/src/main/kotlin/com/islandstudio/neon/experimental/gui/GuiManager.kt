package com.islandstudio.neon.experimental.gui

import com.islandstudio.neon.Neon
import com.islandstudio.neon.command.processing.CommandSyntax
import com.islandstudio.neon.command.processing.CommandSyntaxHandler
import com.islandstudio.neon.core.initialization.NeonPluginLoader
import com.islandstudio.neon.player.session.PlayerSessionManager
import com.islandstudio.neon.shared.core.IRunner
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.exception.NeonException
import com.islandstudio.neon.util.ServerUtil
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.server.ServerCommandEvent
import org.koin.core.annotation.Single
import org.koin.core.component.inject
import kotlin.reflect.KClass

@Single
class GuiManager: IComponentInjector {
    private val neon by inject<Neon>()
    private val guiSessions: HashSet<GuiSession<*>> = hashSetOf()
    private val playerSessionManager by inject<PlayerSessionManager>()

    companion object: IRunner {
        override fun run() {
            NeonPluginLoader.registerEventProcessor(EventProcessor())
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: GuiConstructor<*>>initGuiSession(player: Player, guiClass: KClass<T>): GuiSession<T> {
        return guiSessions.find { x -> x.player == player }?.let { it as GuiSession<T> } ?: GuiSession(player, guiClass).also {
            guiSessions.add(it)
        }
    }

    fun getGuiSession(player: Player): GuiSession<*>? {
        return guiSessions.find { x -> x.player == player }
    }

    private fun discardGuiSession(e: InventoryCloseEvent) {
        val player = e.player as Player

        getGuiSession(player)?.let {
            if (e.view.title != it.getGui().getGuiName()) return

            val inventoryHolder = e.inventory.holder ?: return

            if (inventoryHolder !is GuiConstructor<*>) return

            val currentGuiState = it.getGui().getCurrentGuiState()

            if (currentGuiState.isStateActive()) {
                currentGuiState.keepStateActive(false)
                return
            }

            guiSessions.removeIf { x -> x.player == player }
        }
    }

    private fun forceCloseAllPlayerInventory(commandEvent: Event) {
        ServerUtil.onServerReload(commandEvent) {
            neon.server.onlinePlayers.forEach {
                it.closeInventory()
            }
        }
    }

    private class EventProcessor: Listener, IComponentInjector {
        private val guiManager by inject<GuiManager>()

        @EventHandler
        private fun onInventoryClick(e: InventoryClickEvent) {
            val player: Player = e.whoClicked as Player

            guiManager.getGuiSession(player)?.let {
                if (!it.matchesGui(e.view.title)) return

                val clickedInventory = e.clickedInventory ?: return
                val inventoryHolder = clickedInventory.holder ?: return

                if (clickedInventory == player.inventory) e.isCancelled = true

                if (inventoryHolder !is GuiConstructor<*>) return

                e.isCancelled = true

                if (e.currentItem == null) return

                runCatching {
                    inventoryHolder.setGuiClickHandler(e)
                }.onFailure { throwable ->
                    player.closeInventory()
                    CommandSyntaxHandler.sendCommandSyntax(player, CommandSyntax.UNEXPECTED_GUI_ERROR)
                    throw NeonException(throwable.message, throwable)
                }
            }
        }

        @EventHandler
        private fun onInventoryClose(e: InventoryCloseEvent) {
            guiManager.discardGuiSession(e)
        }

        @EventHandler
        private fun onServerCommandExecute(e: ServerCommandEvent) {
            guiManager.forceCloseAllPlayerInventory(e)
        }

        @EventHandler
        private fun onPlayerCommandPreExecute(e: PlayerCommandPreprocessEvent) {
            guiManager.forceCloseAllPlayerInventory(e)
        }
    }
}