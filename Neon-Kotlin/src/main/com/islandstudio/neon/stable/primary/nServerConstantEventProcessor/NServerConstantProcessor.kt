package com.islandstudio.neon.stable.primary.nServerConstantEventProcessor

import com.islandstudio.neon.stable.core.network.NPacketProcessor
import com.islandstudio.neon.stable.primary.nCommand.CommandSyntax
import com.islandstudio.neon.stable.primary.nConstructor.NConstructor
import com.islandstudio.neon.stable.secondary.nDurable.NDurable
import com.islandstudio.neon.stable.secondary.nRank.NRank
import com.islandstudio.neon.stable.utils.nGUI.NGUI
import com.islandstudio.neon.stable.utils.reflection.NMSRemapped
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.crafting.Recipe
import org.bukkit.ChatColor
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.ServerCommandEvent
import org.bukkit.event.server.ServerLoadEvent

object NServerConstantProcessor {
    object Handler {
        /**
         * Initialization for nServerConstantProcessor
         *
         */
        fun run() = NConstructor.registerEventProcessor(EventProcessor())
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

        NPacketProcessor.sendGamePacket(
            player, ClientboundUpdateRecipesPacket(
                serverRecipes.values.parallelStream().flatMap { map -> map.values.parallelStream() }
                    .toList()!! as MutableCollection<Recipe<*>>
            )
        )

        /* Recipe book update */
        val playerRecipeBook: Any = nPlayer.javaClass.getMethod(NMSRemapped.Mapping.NMS_PLAYER_RECIPE_BOOK.remapped).invoke(nPlayer)

        playerRecipeBook.javaClass.getMethod(NMSRemapped.Mapping.NMS_INIT_RECIPE_BOOK.remapped, ServerPlayer::class.java).invoke(playerRecipeBook, nPlayer)
    }

    private class EventProcessor: Listener {
        @EventHandler
        private fun onServerLoad(e: ServerLoadEvent) {
            if (!(e.type == ServerLoadEvent.LoadType.STARTUP || e.type == ServerLoadEvent.LoadType.RELOAD)) return

            NConstructor.plugin.server.onlinePlayers.parallelStream().forEach {player ->
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
            if (e.sender is Player) { if (!e.sender.isOp) return }

            if (!(e.command.equals("rl", true) || e.command.equals("reload", true))) return

            NConstructor.plugin.server.onlinePlayers.parallelStream().forEach { it.closeInventory() }
        }
    }
}