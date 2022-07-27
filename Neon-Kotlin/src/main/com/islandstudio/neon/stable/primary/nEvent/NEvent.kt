package com.islandstudio.neon.stable.primary.nEvent

import com.islandstudio.neon.experimental.nEffect.NEffect
import com.islandstudio.neon.stable.secondary.nWaypoints.NWaypoints
import com.islandstudio.neon.stable.utils.nGUI.NGUI
import org.bukkit.Material
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.*
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class NEvent : Listener {
    @EventHandler
    fun onPlayerAnimation(e: PlayerAnimationEvent) {
        if (e.player.inventory.itemInMainHand.type == Material.DIAMOND_AXE) {
            if (e.animationType == PlayerAnimationType.ARM_SWING) {
                e.isCancelled = true
            }
        }
        e.isCancelled = true
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

        NEffect.closeInventory(e)
    }

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        val player: Player = e.whoClicked as Player

        NWaypoints.GUIHandlerCreation(NGUI.Handler.getNGUI(player)).setEventHandler(e)
        NWaypoints.GUIHandlerRemoval(NGUI.Handler.getNGUI(player)).setEventHandler(e)

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