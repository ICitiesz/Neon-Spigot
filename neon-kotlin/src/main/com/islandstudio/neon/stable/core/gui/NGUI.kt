package com.islandstudio.neon.stable.core.gui

import com.islandstudio.neon.stable.core.application.init.AppInitializer
import com.islandstudio.neon.stable.core.gui.structure.GUIConstructor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

object NGUI {
    private val guiSessions: HashMap<Player, GUISession> by lazy { HashMap() }

    object Handler {
        fun run() {
            AppInitializer.registerEventProcessor(EventProcessor())
        }
    }

    fun initGUISession(player: Player, guiHandler: Class<out GUIConstructor>): GUISession {
        guiSessions[player]?.let {
            return it
        }

        guiSessions[player] = GUISession(player, guiHandler)

        return guiSessions[player]!!
    }

    fun getGUISession(player: Player): GUISession? {
        return guiSessions[player]
    }

    private fun discardGUISession(player: Player) {
        guiSessions.remove(player)
    }

    private fun processGUIClosing(e: InventoryCloseEvent) {
        val player: Player = e.player as Player

        getGUISession(player)?.let {
            if (e.view.title != it.getGUIHandler().getGUIName()) return

            val inventoryHolder: InventoryHolder = e.inventory.holder ?: return

            if (inventoryHolder !is GUIConstructor) return

            if (it.getGUIHandler().guiState.isStateActive()) {
                it.getGUIHandler().guiState.keepStateActive(false)
                return
            }

            discardGUISession(player)
        }
    }

    private class EventProcessor: Listener {
        @EventHandler
        private fun onInventoryClick(e: InventoryClickEvent) {
            val player: Player = e.whoClicked as Player

            getGUISession(player)?.let {
                if (!it.matchesGUI(e.view.title)) return

                val clickedInventory: Inventory = e.clickedInventory ?: return
                val inventoryHolder: InventoryHolder = clickedInventory.holder ?: return

                if (clickedInventory == player.inventory) e.isCancelled = true
                if (inventoryHolder !is GUIConstructor) return

                e.isCancelled = true

                if (e.currentItem == null) return

                inventoryHolder.setGUIClickHandler(e)
            }
        }

        @EventHandler
        private fun onInventoryClose(e: InventoryCloseEvent) {
            processGUIClosing(e)
        }
    }
}