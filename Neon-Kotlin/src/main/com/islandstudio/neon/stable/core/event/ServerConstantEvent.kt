package com.islandstudio.neon.stable.core.event

import com.islandstudio.neon.Neon
import com.islandstudio.neon.experimental.nEffect.NEffect
import com.islandstudio.neon.stable.core.application.di.ModuleInjector
import com.islandstudio.neon.stable.core.application.init.NConstructor
import com.islandstudio.neon.stable.core.application.reflection.NReflector
import com.islandstudio.neon.stable.core.application.reflection.mapping.NMSMapping
import com.islandstudio.neon.stable.core.application.reflection.remastered.NmsMap
import com.islandstudio.neon.stable.core.application.server.NPacketProcessor
import com.islandstudio.neon.stable.core.command.NCommand
import com.islandstudio.neon.stable.features.nDurable.NDurable
import com.islandstudio.neon.stable.features.nRank.NRank
import com.islandstudio.neon.stable.features.nWaypoints.NWaypoints
import com.islandstudio.neon.stable.primary.nServerFeatures.NServerFeatures
import com.islandstudio.neon.stable.utils.nGUI.NGUI
import org.bukkit.ChatColor
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.ServerCommandEvent
import org.bukkit.event.server.ServerLoadEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.koin.core.component.inject

class ServerConstantEvent: ModuleInjector {
    private val neon by inject<Neon>()

    private val reloadCommands = arrayListOf("rl", "reload", "bukkit:reload", "bukkit:rl")
    private val reloadCommandsWithConfirm = arrayListOf("rl confirm", "reload confirm", "bukkit:reload confirm", "bukkit:rl confirm")
    private val serverName = neon.server.name
    private val doLetMeReload: Boolean? = with(System.getProperties()) {
        return@with this.entries.find { it.key == "LetMeReload" }?.let {
            (it.value as String).toBoolean()
        }
    }

    private enum class PlayerHandshakeStatus {
        JOINING,
        LEAVING
    }

    object Handler {
        fun run() {
            NConstructor.registerEventProcessor(EventProcessor())
            NConstructor.registerEventProcessor(NServerFeatures.EventProcessor())
        }
    }

    private fun broadcastPlayerNotification(player: Player, playerHandshakeStatus: PlayerHandshakeStatus) {
        val server = neon.server

        when (playerHandshakeStatus) {
            PlayerHandshakeStatus.JOINING -> {
                server.broadcastMessage(
                    NCommand.COMMAND_SYNTAX_PREFIX +
                        "${ChatColor.GOLD}Welcome back, ${ChatColor.GREEN}${player.name}${ChatColor.GOLD}!")
                server.broadcastMessage(
                    NCommand.COMMAND_SYNTAX_PREFIX +
                    "${ChatColor.GREEN}${server.onlinePlayers.size}${ChatColor.GOLD} of ${ChatColor.RED}${server.maxPlayers}${ChatColor.GOLD} player(s) Online!"
                )
            }

            PlayerHandshakeStatus.LEAVING -> {
                server.broadcastMessage("${NCommand.COMMAND_SYNTAX_PREFIX}${ChatColor.GREEN}${player.name}${ChatColor.GOLD} left," +
                        " ${ChatColor.GREEN}${server.onlinePlayers.size - 1}${ChatColor.GOLD} other(s) here!")
            }
        }
    }

    /**
     * Update player recipes once the server reloaded.
     *
     * @param player The player to update the recipes for. (Player)
     */
    @Suppress("UNCHECKED_CAST")
    private fun updatePlayerRecipe(player: Player) {
        val mcPlayer = NPacketProcessor.getNPlayer(player)
        val mcServer = mcPlayer.javaClass.getField(NMSMapping.NMS_MC_SERVER.remapped).get(mcPlayer)
        val craftingManager = mcServer.javaClass.getMethod(NMSMapping.NMS_CRAFTING_MANAGER.remapped).invoke(mcServer)!!

        val serverRecipes: Map<Any, Map<Any, Any>> = craftingManager.javaClass.getField(NMSMapping.NMS_SERVER_RECIPES.remapped)
            .get(craftingManager)!! as Map<Any, Map<Any, Any>>

        val recipeList = serverRecipes.values.parallelStream().flatMap { map -> map.values.parallelStream() }
            .toList()!!

        val updateRecipePacketConstructors = NReflector.getMcClass(
            "network.protocol.game.${NMSMapping.NMS_CLIENT_PACKET_UPDATE_RECIPES.remapped}"
        )!!.constructors.filter { it.parameters.size == 1 }

        val recipeUpdatePacket: Any = updateRecipePacketConstructors.find { it.parameterTypes.contains(Collection::class.java) }!!
            .newInstance(recipeList as MutableCollection<*>)

        NPacketProcessor.sendGamePacket(player, recipeUpdatePacket)

        /* Recipe book update */
        val playerRecipeBook: Any = mcPlayer.javaClass.getMethod(NmsMap.PlayerRecipeBook.remapped).invoke(mcServer)

        playerRecipeBook.javaClass.getMethod(NmsMap.InitRecipeBook.remapped).invoke(playerRecipeBook, mcPlayer)
    }

    /**
     * Perform inventory closing upon server reload to avoid any unexpected errors.
     *
     * @param e
     */
    private fun closePlayerInventory(e: Event) {
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

        if (!(command in reloadCommands || command in reloadCommandsWithConfirm)) {
            return
        }

        with(serverName) {
            when {
                this.equals("Bukkit", true) -> return@with

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

        neon.server.onlinePlayers.forEach { player ->
            player.closeInventory()
        }
    }

    private class EventProcessor: Listener, ModuleInjector {
        private val neon by inject<Neon>()
        private val serverConstantEvent = ServerConstantEvent()

        @EventHandler
        private fun onServerLoad(e: ServerLoadEvent) {
            when (e.type) {
                ServerLoadEvent.LoadType.STARTUP, ServerLoadEvent.LoadType.RELOAD -> {
                    neon.server.onlinePlayers.forEach { player ->
                        serverConstantEvent.updatePlayerRecipe(player)
                        NPacketProcessor.reloadGamePacketListener(player)
                    }
                }
            }
        }

        @EventHandler
        private fun onServerCommandSend(e: ServerCommandEvent) {
            serverConstantEvent.closePlayerInventory(e)
        }

        @EventHandler
        private fun onPlayerCommandPreprocess(e: PlayerCommandPreprocessEvent) {
            serverConstantEvent.closePlayerInventory(e)
        }

        @EventHandler
        private fun onPlayerJoin(e: PlayerJoinEvent) {
            with(e.player) {
                NPacketProcessor.addGamePacketListener(this)
                NRank.updateTag()
                NDurable.toggleDamageProperty(NDurable.isEnabled(), this)
                e.joinMessage = ("")
                serverConstantEvent.broadcastPlayerNotification(this, PlayerHandshakeStatus.JOINING)
            }
        }

        @EventHandler
        private fun onPlayerQuit(e: PlayerQuitEvent) {
            with(e.player) {
                NPacketProcessor.removeGamePacketListener(this)
                NGUI.Handler.nGUIContainer.remove(player)
                e.quitMessage = ""
                serverConstantEvent.broadcastPlayerNotification(this, PlayerHandshakeStatus.LEAVING)
            }
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
        private fun onInventoryClose(e: InventoryCloseEvent) {
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
        private fun onInventoryClick(e: InventoryClickEvent) {
            val player: Player = e.whoClicked as Player

            NWaypoints.GUIHandlerCreation(NGUI.Handler.getNGUI(player)).setEventHandler(e)
            NWaypoints.GUIHandlerRemoval(NGUI.Handler.getNGUI(player)).setEventHandler(e)

            NEffect.setEventHandler(e)
        }

        @EventHandler
        private fun onItemDrop(e: PlayerDropItemEvent) {
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