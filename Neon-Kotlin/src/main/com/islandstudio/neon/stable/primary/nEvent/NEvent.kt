package com.islandstudio.neon.stable.primary.nEvent

import com.islandstudio.neon.Neon
import com.islandstudio.neon.experimental.nBundle.NBundle
import com.islandstudio.neon.experimental.nEffect.NEffect
import com.islandstudio.neon.stable.primary.nExperimental.NExperimental
import com.islandstudio.neon.stable.secondary.nHarvest.NHarvest
import com.islandstudio.neon.stable.secondary.nWaypoints.NWaypoints
import com.islandstudio.neon.stable.secondary.nRank.NRank
import com.islandstudio.neon.stable.utils.ServerHandler
import com.islandstudio.neon.stable.primary.nProfile.NProfile
import com.islandstudio.neon.experimental.nDurable.NDurable
import com.islandstudio.neon.stable.utils.nGUI.NGUI
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.*
import org.bukkit.event.player.*
import org.bukkit.event.server.ServerLoadEvent
import org.bukkit.event.world.LootGenerateEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin.getPlugin

class NEvent : Listener {
    private val plugin: Plugin = getPlugin(Neon::class.java)

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
        NDurable.setWoodStripping(e)
    }

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
    fun lootGenerate(e: LootGenerateEvent) {
        NBundle.setSpawning(e)
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
        //NRepair.test(e)
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

    @EventHandler
    fun onInventoryEvent(e: CraftItemEvent) {
        //NRepair.test(e)
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        NDurable.setBlockBreaking(e)
    }

    @EventHandler
    fun onBlockDamage(e: BlockDamageEvent) {
        NDurable.getInstantBreak(e)
    }

    @EventHandler
    fun onItemDamageEvent(e: PlayerItemDamageEvent) {
        NDurable.itemDamage(e)
    }

    @EventHandler
    fun onPrepareAnvil(e: PrepareAnvilEvent) {
        NDurable.repairItem(e)
    }

    @EventHandler
    fun onEntityDamageByEntity(e: EntityDamageByEntityEvent) {
        NDurable.setAttackDamage(e)
    }

    @EventHandler
    fun onInventoryOpen(e: InventoryOpenEvent) {
        val inventory: Inventory = e.inventory

        //println(inventory.type)
        //println(inventory is PlayerInventory)

        inventory.contents.forEach {
            if (it == null) return@forEach

            val item: Material = it.type
            val itemName: String = it.type.name

            when {
                itemName.contains("_PICKAXE") || itemName.contains("_AXE")
                        || itemName.contains("_SHOVEL") || itemName.contains("_SWORD")
                        || itemName.contains("_HOE") -> {
                    val itemMeta: Damageable = (it.itemMeta as Damageable?)!!

                    itemMeta.damage = item.maxDurability - 1
                    itemMeta.lore = listOf("${ChatColor.RED}BROKEN")

                    it.itemMeta = itemMeta
                }
            }

            //(e.player as Player).updateInventory()
        }
    }
}