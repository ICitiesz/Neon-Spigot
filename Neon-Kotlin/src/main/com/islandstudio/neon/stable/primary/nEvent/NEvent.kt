package com.islandstudio.neon.stable.primary.nEvent

import com.islandstudio.neon.Main
import com.islandstudio.neon.experimental.nEffect.NEffect
import com.islandstudio.neon.stable.primary.nExperimental.NExperimental
import com.islandstudio.neon.stable.secondary.nHarvest.NHarvest
import com.islandstudio.neon.stable.secondary.nWaypoints.NWaypoints
import com.islandstudio.neon.stable.secondary.nRank.NRank
import com.islandstudio.neon.stable.utils.ServerHandler
import com.islandstudio.neon.stable.primary.nProfile.NProfile
import com.islandstudio.neon.stable.utils.nGUI.NGUI
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.*
import org.bukkit.event.server.ServerLoadEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin.getPlugin

class NEvent : Listener {
    private val plugin: Plugin = getPlugin(Main::class.java)

    @EventHandler
    fun onServerLoad(e: ServerLoadEvent) {
        if (e.type != ServerLoadEvent.LoadType.RELOAD) return

        plugin.server.onlinePlayers.parallelStream().forEach { player ->
            ServerHandler.updateRecipe(player)
            NHarvest.addPlayer(player)
        }
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player: Player = e.player

        NProfile.Handler.createProfile(player)
        NRank.updateTag()
        NHarvest.addPlayer(player)

        ServerHandler.broadcastJoinMessage(e)
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        val player: Player = e.player

        NGUI.Handler.nGUIContainer.remove(player)
        NHarvest.removePlayer(player)
        ServerHandler.broadcastQuitMessage(e)
    }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        NHarvest.setEventHandler(e)
    }

    @EventHandler
    fun onPlayerChat(e: AsyncPlayerChatEvent) {
        e.isCancelled = true
        NRank.sendMessage(e.player, e.message)
    }

    @EventHandler
    fun onInventoryClose(e: InventoryCloseEvent) {
        val inventoryName = e.view.title
        val player = e.player as Player

        /* nWaypoints Main GUI */
        if (inventoryName.equals(NWaypoints.GUIHandlerCreation(NGUI.Handler.getNGUI(player)).getGUIName(), true)) {
            if (NWaypoints.GUIHandlerCreation.isClicked) {
                NWaypoints.GUIHandlerCreation.isClicked = false
                return
            }

            NGUI.Handler.nGUIContainer.remove(player)
            NWaypoints.Handler.waypointDataContainer.remove(player.uniqueId.toString())
        }

        /* nWaypoints Removal GUI */
        if (inventoryName.equals(NWaypoints.GUIHandlerRemoval(NGUI.Handler.getNGUI(player)).getGUIName(), true)) {
            if (NWaypoints.GUIHandlerRemoval.isClicked) {
                NWaypoints.GUIHandlerRemoval.isClicked = false
                return
            }

            NWaypoints.GUIHandlerRemoval.removalContainer.remove(player.uniqueId.toString())
            NGUI.Handler.nGUIContainer.remove(player)
            NWaypoints.Handler.waypointDataContainer.remove(player.uniqueId.toString())
        }

        /* nExperimental GUI */
        if (inventoryName.equals(NExperimental.GUIHandler(NGUI.Handler.getNGUI(player)).getGUIName(), true)) {
            NGUI.Handler.nGUIContainer.remove(player)
        }

        NEffect.closeInventory(e)
    }

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        val player: Player = e.whoClicked as Player

        NWaypoints.GUIHandlerCreation(NGUI.Handler.getNGUI(player)).setEventHandler(e)
        NWaypoints.GUIHandlerRemoval(NGUI.Handler.getNGUI(player)).setEventHandler(e)

        NExperimental.GUIHandler(NGUI.Handler.getNGUI(player)).setEventHandler(e)

        NEffect.setEventHandler(e)
    }

    @EventHandler
    fun onItemDrop(e: PlayerDropItemEvent) {
        val droppedItem: Item = e.itemDrop
        val itemStack: ItemStack = droppedItem.itemStack
        val itemMeta: ItemMeta = itemStack.itemMeta!!

        if (itemMeta.hasDisplayName() && itemMeta.displayName.equals(NEffect.EFFECT_1, true)
            || itemMeta.displayName.equals(NEffect.EFFECT_2, true)
            || itemMeta.displayName.equals(NEffect.EFFECT_3, true) || itemMeta.displayName.equals(NEffect.REMOVE_BUTTON, true)) {
            droppedItem.remove()
        }
    }
}