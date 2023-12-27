package com.islandstudio.neon.stable.core.event

import com.islandstudio.neon.experimental.nEffect.NEffect
import com.islandstudio.neon.stable.core.init.NConstructor
import com.islandstudio.neon.stable.core.network.NPacketProcessor
import com.islandstudio.neon.stable.primary.nCommand.CommandSyntax
import com.islandstudio.neon.stable.primary.nServerFeatures.NServerFeatures
import com.islandstudio.neon.stable.secondary.nDurable.NDurable
import com.islandstudio.neon.stable.secondary.nRank.NRank
import com.islandstudio.neon.stable.secondary.nWaypoints.NWaypoints
import com.islandstudio.neon.stable.utils.nGUI.NGUI
import com.islandstudio.neon.stable.utils.reflection.NMSRemapped
import com.islandstudio.neon.stable.utils.reflection.NReflector
import net.minecraft.server.level.ServerPlayer
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Server
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.*
import org.bukkit.event.server.ServerCommandEvent
import org.bukkit.event.server.ServerLoadEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

object NServerConstantProcessor {
    object Handler {
        /**
         * Initialization for nServerConstantProcessor
         *
         */
        fun run() {
            NConstructor.registerEventProcessor(EventProcessor())
            NConstructor.registerEventProcessor(NServerFeatures.EventProcessor())
        }
    }

    /**
     * Broadcast join message to all players when a player join the server.
     *
     * @param player The player who join the server.
     */
    fun broadcastPlayerJoinMessage(player: Player) {
        val server: Server = player.server

        server.broadcastMessage(CommandSyntax.createSyntaxMessage("${ChatColor.GOLD}Welcome back, ${ChatColor.GREEN}${player.name}${ChatColor.GOLD}!"))
        server.broadcastMessage(CommandSyntax.createSyntaxMessage("${ChatColor.GREEN}${server.onlinePlayers.size}${ChatColor.GOLD} of ${ChatColor.RED}${server.maxPlayers}${ChatColor.GOLD} player(s) Online!"))
    }

    /**
     * Broadcast quit message to all players when a player quit the server.
     *
     * @param player The player who quit the server.
     */
    fun broadcastPlayerQuitMessage(player: Player) {
        val server: Server = player.server

        server.broadcastMessage(CommandSyntax.createSyntaxMessage("${ChatColor.GREEN}${player.name}${ChatColor.GOLD} left, ${ChatColor.GREEN}${server.onlinePlayers.size - 1}${ChatColor.GOLD} other(s) here!"))
    }

    /**
     * Get the mode, either Online Mode or Offline Mode.
     *
     * @return The mode. (String)
     */
    fun getMode(): String {
        return if (NConstructor.plugin.server.onlineMode) {
            "online"
        } else {
            "offline"
        }
    }

    /**
     * Update player recipes once the server reloaded.
     *
     * @param player The player to update the recipes for. (Player)
     */
    @Suppress("UNCHECKED_CAST")
    private fun updateRecipe(player: Player) {
        val nPlayer = NPacketProcessor.getNPlayer(player)
        val serverRecipes: Map<Any, Map<Any, Any>>

        val mcServer = nPlayer.javaClass.getField(NMSRemapped.Mapping.NMS_MC_SERVER.remapped).get(nPlayer)
        val craftingManager: Any = mcServer.javaClass.getMethod(NMSRemapped.Mapping.NMS_CRAFTING_MANAGER.remapped).invoke(
            mcServer)!!

        serverRecipes = craftingManager.javaClass.getField(NMSRemapped.Mapping.NMS_SERVER_RECIPES.remapped).get(craftingManager)!! as Map<Any, Map<Any, Any>>

        val recipeList = serverRecipes.values.parallelStream().flatMap { map -> map.values.parallelStream() }.toList()!!
        val updateRecipePacketConstructors = NReflector
            .getNamespaceClass("network.protocol.game.${NMSRemapped.Mapping.NMS_CLIENT_PACKET_UPDATE_RECIPES.remapped}")!!.constructors.filter { it.parameterCount == 1 }

        val recipeUpdatePacket: Any =
            updateRecipePacketConstructors.find { it.parameterTypes.contains(Collection::class.java) }!!.newInstance(recipeList as MutableCollection<*>)

        NPacketProcessor.sendGamePacket(player, recipeUpdatePacket)

        /* Recipe book update */
        val playerRecipeBook: Any = nPlayer.javaClass.getMethod(NMSRemapped.Mapping.NMS_PLAYER_RECIPE_BOOK.remapped).invoke(nPlayer)

        playerRecipeBook.javaClass.getMethod(NMSRemapped.Mapping.NMS_INIT_RECIPE_BOOK.remapped, ServerPlayer::class.java).invoke(playerRecipeBook, nPlayer)
    }

    private class EventProcessor: Listener {
        @EventHandler
        private fun onServerLoad(e: ServerLoadEvent) {
            if (!(e.type == ServerLoadEvent.LoadType.STARTUP || e.type == ServerLoadEvent.LoadType.RELOAD)) return

            NConstructor.plugin.server.onlinePlayers.parallelStream().forEach { player ->
                updateRecipe(player)
                NPacketProcessor.reloadGamePacketListener(player)
            }
        }

        @EventHandler
        private fun onPlayerJoin(e: PlayerJoinEvent) {
            val player: Player = e.player

            NPacketProcessor.addGamePacketListener(player)
            NRank.updateTag()
            NDurable.toggleDamageProperty(NDurable.isEnabled(), player)
            e.joinMessage = ""
            broadcastPlayerJoinMessage(player)
        }

        @EventHandler
        private fun onPlayerQuit(e: PlayerQuitEvent) {
            val player: Player = e.player

            NPacketProcessor.removeGamePacketListener(player)
            NGUI.Handler.nGUIContainer.remove(player)
            e.quitMessage = ""
            broadcastPlayerQuitMessage(player)
        }

        @EventHandler
        private fun onServerCommandSend(e: ServerCommandEvent) {
            if (e.sender is Player) {
                if (!e.sender.isOp) return
            }

            if (!(e.command.equals("rl", true) || e.command.equals("reload", true))) return

            NConstructor.plugin.server.onlinePlayers.parallelStream().forEach { it.closeInventory() }
        }

        @EventHandler
        private fun onInventoryOpen(e: InventoryOpenEvent) {
            if (e.view.title == NEffect.inventoryName) return

            e.inventory.contents.filterNotNull().forEach {
                if (NDurable.isEnabled()) {
                    NDurable.Handler.applyDamageProperty(it, 0)
                    return@forEach
                }

               NDurable.Handler.removeDamageProperty(it, true)
            }
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
}