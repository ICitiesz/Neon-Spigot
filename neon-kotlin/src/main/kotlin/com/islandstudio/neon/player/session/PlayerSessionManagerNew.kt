package com.islandstudio.neon.player.session

import com.islandstudio.neon.command.processing.CommandSyntaxHandler
import com.islandstudio.neon.core.nmsmapping.NmsManagerNew
import com.islandstudio.neon.core.nmsmapping.type.NmsConstructor
import com.islandstudio.neon.core.nmsmapping.type.NmsField
import com.islandstudio.neon.core.nmsmapping.type.NmsMethod
import com.islandstudio.neon.server.ServerGamePacketManagerNew
import com.islandstudio.neon.shared.core.di.IComponentProvider
import com.islandstudio.neon.shared.core.di.getComponent
import com.islandstudio.neon.shared.core.initialization.IPluginContext
import com.islandstudio.neon.shared.core.initialization.IRunnerNew
import com.islandstudio.neon.shared.utils.data.DataUtil
import net.minecraft.server.level.ServerPlayer
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.ServerLoadEvent

class PlayerSessionManagerNew: IRunnerNew, IComponentProvider, NmsManagerNew.INmsMapper {
    private val pluginContext = getComponent<IPluginContext>()
    override fun run() {
        registerEvent(PlayerSessionManagerNewEvent(this))
    }

    /**
     * Update player recipes once the server reloaded.
     *
     * @param player The player to update the recipes for. (Player)
     */
    private fun updatePlayerRecipe(player: Player) {
        val nmsPlayer = NmsManagerNew.toNmsPlayer(player)
        val nmsServer = nmsPlayer.javaClass.getField(mapField(NmsField.McServer)).get(nmsPlayer)
        val craftingManager = nmsServer.javaClass.getMethod(mapMethod(NmsMethod.CraftingManager)).invoke(nmsServer)

        val serverRecipes = DataUtil.asType<Map<Any, Map<Any, Any>>>(
            craftingManager.javaClass.getField(mapField(NmsField.ServerRecipes)).get(craftingManager)
        )
        val recipeList = serverRecipes.values.parallelStream().flatMap { map -> map.values.parallelStream() }.toList()

        val updateRecipePacketConstructors = NmsManagerNew.getNmsClass("network.protocol.game.${mapConstructor(NmsConstructor.ClientPacketUpdateRecipes)}")
            ?.let {
                it.constructors.filter { constructor -> constructor.parameters.size == 1 }
            }

        val updateRecipePacket = updateRecipePacketConstructors?.find { it.parameterTypes.contains(Collection::class.java) }
            ?.newInstance(DataUtil.asType<MutableCollection<*>>(recipeList))

        updateRecipePacket?.let {
            ServerGamePacketManagerNew.sendServerGamePacket(player, it)
        }

        /* Recipe book update */
        val playerRecipeBook = nmsPlayer.javaClass.getMethod(mapMethod(NmsMethod.InitRecipeBook)).invoke(nmsPlayer)

        playerRecipeBook.javaClass.getMethod(mapMethod(NmsMethod.InitRecipeBook), ServerPlayer::class.java)
            .invoke(playerRecipeBook, nmsPlayer)
    }

    private fun broadcastPlayerSessionMessage(player: Player, playerSessionState: PlayerSessionState) {
        val server = pluginContext.getServer()

        // TODO: Need to revamp the message in the future
        when(playerSessionState) {
            PlayerSessionState.OnJoining ->  {
                server.broadcastMessage(
                    CommandSyntaxHandler.COMMAND_SYNTAX_PREFIX +
                            "${ChatColor.GOLD}Welcome back, ${ChatColor.GREEN}${player.name}${ChatColor.GOLD}!")
                server.broadcastMessage(
                    CommandSyntaxHandler.COMMAND_SYNTAX_PREFIX +
                            "${ChatColor.GREEN}${server.onlinePlayers.size}${ChatColor.GOLD} of ${ChatColor.RED}${server.maxPlayers}${ChatColor.GOLD} player(s) Online!"
                )
            }

            PlayerSessionState.OnLeaving -> {
                server.broadcastMessage("${CommandSyntaxHandler.COMMAND_SYNTAX_PREFIX}${ChatColor.GREEN}${player.name}${ChatColor.GOLD} left," +
                        " ${ChatColor.GREEN}${server.onlinePlayers.size - 1}${ChatColor.GOLD} other(s) here!")
            }
        }
    }

    private enum class PlayerSessionState {
        OnJoining,
        OnLeaving
    }

    private class PlayerSessionManagerNewEvent(private val playerSessionManagerNew: PlayerSessionManagerNew): Listener {
        private val pluginContext = playerSessionManagerNew.pluginContext

        @EventHandler
        private fun onServerLoad(e: ServerLoadEvent) {
            when(e.type) {
                ServerLoadEvent.LoadType.STARTUP, ServerLoadEvent.LoadType.RELOAD -> {
                    pluginContext.getServer().onlinePlayers.forEach { player ->
                        ServerGamePacketManagerNew.registerServerGamePacketListener(player)
                    }
                }
            }
        }

        @EventHandler
        private fun onPlayerJoin(e: PlayerJoinEvent) {
            val player = e.player

            ServerGamePacketManagerNew.registerServerGamePacketListener(player)

            /* Player join message */
            e.joinMessage = ""
            playerSessionManagerNew.broadcastPlayerSessionMessage(player, PlayerSessionState.OnJoining)
        }

        @EventHandler
        private fun onPlayerLeave(e: PlayerQuitEvent) {
            val player = e.player

            ServerGamePacketManagerNew.unregisterServerGamePacketListener(player)

            /* Player quit message */
            e.quitMessage = ""
            playerSessionManagerNew.broadcastPlayerSessionMessage(player, PlayerSessionState.OnLeaving)
        }
    }
}