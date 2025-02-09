package com.islandstudio.neon.stable.player.session

import com.islandstudio.neon.Neon
import com.islandstudio.neon.api.adapter.PlayerProfileAdapter
import com.islandstudio.neon.api.dto.action.ActionStatus
import com.islandstudio.neon.api.dto.request.player.CreatePlayerProfileRequestDTO
import com.islandstudio.neon.api.dto.request.player.GetPlayerProfileRequestDTO
import com.islandstudio.neon.api.dto.request.player.UpdatePlayerProfileRequestDTO
import com.islandstudio.neon.api.dto.request.security.AssignRoleRequestDTO
import com.islandstudio.neon.api.dto.request.security.UnassignRoleRequestDTO
import com.islandstudio.neon.api.entity.player.PlayerProfileEntity
import com.islandstudio.neon.shared.core.AppContext
import com.islandstudio.neon.shared.core.IRunner
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.server.ServerRunningMode
import com.islandstudio.neon.shared.utils.data.IObjectMapper
import com.islandstudio.neon.shared.utils.serialization.ObjectSerializer
import com.islandstudio.neon.stable.core.application.AppLoader
import com.islandstudio.neon.stable.core.application.datakey.DataContainerManager
import com.islandstudio.neon.stable.core.application.datakey.DataContainerType
import com.islandstudio.neon.stable.core.application.reflection.NmsProcessor
import com.islandstudio.neon.stable.core.application.reflection.mapping.NmsMap
import com.islandstudio.neon.stable.core.application.server.ServerGamePacketManager
import com.islandstudio.neon.stable.core.command.NCommand
import net.minecraft.server.level.ServerPlayer
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.ServerLoadEvent
import org.koin.core.annotation.Single
import org.koin.core.component.inject

@Single
class PlayerSessionManager: IComponentInjector, IObjectMapper {
    private val neon by inject<Neon>()
    private val appContext by inject<AppContext>()
    private val playerProfileAdapter by inject<PlayerProfileAdapter>()

    companion object: IRunner {
        private val eventProcessor = EventProcessor()

        override fun run() {
            AppLoader.Companion.registerEventProcessor(eventProcessor)
        }
    }

    fun createPlayerSession(player: Player, playerProfile: PlayerProfileEntity) {
        val playerSessionData = ObjectSerializer.serializeToByteArray(
            PlayerSession(
            playerProfile.playerUuid!!,
            playerProfile.playerName!!,
            playerProfile.roleId
            )
        )

        DataContainerManager.attachData(player, playerSessionData, DataContainerType.PlayerSessionContainer)
    }

    fun discardPlayerSession(player: Player) {
        DataContainerManager.detachData(player, DataContainerType.PlayerSessionContainer)
    }

    fun updatePlayerSession(player: Player, newPlayerSession: PlayerSession) {
        val playerSessionData = ObjectSerializer.serializeToByteArray(newPlayerSession)

        DataContainerManager.updateAttachedData(player, playerSessionData, DataContainerType.PlayerSessionContainer)
    }

    fun getPlayerSession(player: Player): PlayerSession? {
        val playerSessionData = DataContainerManager.getAttachedData(player, DataContainerType.PlayerSessionContainer)
            ?: return null

        return ObjectSerializer.deserialzeFromByteArray<PlayerSession>(playerSessionData)
    }

    fun assignPlayerRole(assigner: Player?, target: Player, roleCode: String) {
        val request = AssignRoleRequestDTO(target.uniqueId, roleCode)

        playerProfileAdapter.assignRole(assigner?.name, request).apply {
            when (this.status) {
                ActionStatus.RECORD_NOT_EXIST,
                ActionStatus.PLAYER_NOT_EXIST,
                ActionStatus.DUPLICATE_RECORD -> {
                    println(this.displayMessage)
                }

                ActionStatus.FAILURE -> {
                    println(this.logMessage)
                }

                ActionStatus.SUCCESS -> {
                    val newPlayerSession = getPlayerSession(target)?.copy(roleId = this.result!!) ?: return

                    updatePlayerSession(target, newPlayerSession)
                }

                else -> return
            }
        }
    }

    fun unassignPlayerRole(unassigner: Player?, target: Player) {
        val request = UnassignRoleRequestDTO(target.uniqueId)

        playerProfileAdapter.unassignRole(unassigner?.name, request).apply {
            when (this.status) {
                ActionStatus.RECORD_NOT_EXIST,
                ActionStatus.PLAYER_ROLE_NOT_ASSIGN -> {
                    println(this.displayMessage)
                }

                ActionStatus.FAILURE -> {
                    println(this.logMessage)
                }

                ActionStatus.SUCCESS -> {
                    val newPlayerSession = getPlayerSession(target)?.copy(roleId = null) ?: return

                    updatePlayerSession(target, newPlayerSession)
                }

                else -> return
            }
        }
    }

    fun getAllPlayerNames(): List<String> {
        val onlinePlayers = neon.server.onlinePlayers
        val offlinePlayers = neon.server.offlinePlayers

        val playerNames = ArrayList<String>()

        playerNames.addAll(
            onlinePlayers
                .filter { it.playerProfile.name != null && it.isOnline }
                .map { it.playerProfile.name!! }
        )

        playerNames.addAll(
            offlinePlayers
                .filter { it.playerProfile.name != null && !it.isOnline }
                .map { it.playerProfile.name!! }
        )

        return playerNames
    }

    private fun createPlayerProfile(player: Player) {
        playerProfileAdapter.createPlayerProfile(
            CreatePlayerProfileRequestDTO(player.uniqueId, player.name)
        ).apply {
            when(this.status) {
                ActionStatus.SUCCESS -> {
                    createPlayerSession(player, this.result!!)
                }

                ActionStatus.DUPLICATE_RECORD -> {
                    if (appContext.serverRunningMode != ServerRunningMode.Online) return

                    updatePlayerProfileName(player)?.let {
                        createPlayerSession(player, it)
                    }
                }

                else -> return
            }
        }
    }

    /**
     * Update player profile name when they join server with existing profile
     *
     * @param player
     */
    private fun updatePlayerProfileName(player: Player): PlayerProfileEntity? {
        val playerProfile = playerProfileAdapter.getPlayerProfile(
                GetPlayerProfileRequestDTO(playerUuid = player.uniqueId)
        ).run {
            if (this.status != ActionStatus.SUCCESS) return null

            this.result!!.apply {
                if (this.playerName == player.name) return this

                this.playerName == player.name
            }
        }

        val request = UpdatePlayerProfileRequestDTO(playerProfile.playerUuid!!, playerProfile.playerName!!)

        playerProfileAdapter.updatePlayerProfile(playerProfile.playerName, request).apply {
            return when(this.status) {
                ActionStatus.SUCCESS -> this.result

                ActionStatus.RECORD_NOT_EXIST, ActionStatus.FAILURE -> null

                else -> null
            }
        }
    }

    /**
     * Update player recipes once the server reloaded.
     *
     * @param player The player to update the recipes for. (Player)
     */
    private fun updatePlayerRecipe(player: Player) {
        val mcPlayer = ServerGamePacketManager.getMcPlayer(player)
        val mcServer = mcPlayer.javaClass.getField(NmsMap.McServer.remapped).get(mcPlayer)
        val craftingManager = mcServer.javaClass.getMethod(NmsMap.CraftingManager.remapped).invoke(mcServer)

        @Suppress("UNCHECKED_CAST")
        val serverRecipes: Map<Any, Map<Any, Any>> = craftingManager.javaClass.getField(NmsMap.ServerRecipes.remapped)
            .get(craftingManager)!! as Map<Any, Map<Any, Any>>
        val recipeList = serverRecipes.values.parallelStream().flatMap { map -> map.values.parallelStream() }
            .toList()!!

        val updateRecipePacketConstructors = NmsProcessor().getMcClass(
            "network.protocol.game.${NmsMap.ClientPacketUpdateRecipes.remapped}"
        )!!.constructors.filter { it.parameters.size == 1 }

        val recipeUpdatePacket: Any = updateRecipePacketConstructors.find { it.parameterTypes.contains(Collection::class.java) }!!
            .newInstance(recipeList as MutableCollection<*>)

        ServerGamePacketManager.sendServerGamePacket(player, recipeUpdatePacket)

        /* Recipe book update */
        val playerRecipeBook: Any = mcPlayer.javaClass.getMethod(NmsMap.PlayerRecipeBook.remapped).invoke(mcPlayer)

        playerRecipeBook.javaClass.getMethod(NmsMap.InitRecipeBook.remapped, ServerPlayer::class.java).invoke(playerRecipeBook, mcPlayer)
    }

    private fun broadcastPlayerSessionMessage(player: Player, playerSessionState: PlayerSessionState) {
        val server = neon.server

        // TODO: Need to revamp the message in the future
        when(playerSessionState) {
            PlayerSessionState.OnJoining ->  {
                server.broadcastMessage(
                    NCommand.Companion.COMMAND_SYNTAX_PREFIX +
                            "${ChatColor.GOLD}Welcome back, ${ChatColor.GREEN}${player.name}${ChatColor.GOLD}!")
                server.broadcastMessage(
                    NCommand.Companion.COMMAND_SYNTAX_PREFIX +
                            "${ChatColor.GREEN}${server.onlinePlayers.size}${ChatColor.GOLD} of ${ChatColor.RED}${server.maxPlayers}${ChatColor.GOLD} player(s) Online!"
                )
            }

            PlayerSessionState.OnLeaving -> {
                server.broadcastMessage("${NCommand.Companion.COMMAND_SYNTAX_PREFIX}${ChatColor.GREEN}${player.name}${ChatColor.GOLD} left," +
                        " ${ChatColor.GREEN}${server.onlinePlayers.size - 1}${ChatColor.GOLD} other(s) here!")
            }
        }
    }

    private enum class PlayerSessionState {
        OnJoining,
        OnLeaving
    }

    private class EventProcessor: Listener, IComponentInjector {
        private val neon by inject<Neon>()
        private val playerSessionManager by inject<PlayerSessionManager>()
        private val playerProfileAdapter by inject<PlayerProfileAdapter>()

        @EventHandler
        private fun onServerLoad(e: ServerLoadEvent) {
            when (e.type) {
                ServerLoadEvent.LoadType.STARTUP, ServerLoadEvent.LoadType.RELOAD -> {
                    neon.server.onlinePlayers.forEach { player ->
                        ServerGamePacketManager.reloadServerGamePacketListener(player)
                        playerSessionManager.updatePlayerRecipe(player)
                    }
                }
            }
        }

        @EventHandler
        private fun onPlayerJoin(e: PlayerJoinEvent) {
            val player = e.player

            ServerGamePacketManager.registerServerGamePacketListener(player)
            playerSessionManager.createPlayerProfile(player)

            /* Player join message */
            e.joinMessage = ""
            playerSessionManager.broadcastPlayerSessionMessage(player, PlayerSessionState.OnJoining)
        }

        @EventHandler
        private fun onPlayerQuit(e: PlayerQuitEvent) {
            val player = e.player

            ServerGamePacketManager.unregisterServerGamePacketListener(player)
            playerSessionManager.discardPlayerSession(player)

            /* Player quit message */
            e.quitMessage = ""
            playerSessionManager.broadcastPlayerSessionMessage(player, PlayerSessionState.OnLeaving)
        }
    }
}