package com.islandstudio.neon.player.session

import com.islandstudio.neon.Neon
import com.islandstudio.neon.api.adapter.player.PlayerProfileAdapter
import com.islandstudio.neon.api.dto.action.ActionStatus
import com.islandstudio.neon.api.dto.request.player.CreatePlayerProfileRequestDTO
import com.islandstudio.neon.api.dto.request.player.UpdatePlayerProfileRequestDTO
import com.islandstudio.neon.api.dto.request.security.AssignRoleRequestDTO
import com.islandstudio.neon.api.dto.request.security.UnassignRoleRequestDTO
import com.islandstudio.neon.api.entity.player.PlayerProfileEntity
import com.islandstudio.neon.command.CommandManager
import com.islandstudio.neon.command.processing.CommandSyntaxHandler
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
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.ServerLoadEvent
import org.koin.core.annotation.Single
import org.koin.core.component.inject
import java.util.*

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
        CommandManager.registerPlayerAccessibleCommands(player)
    }

    fun discardPlayerSession(player: Player) {
        CommandManager.unregisterPlayerAccessibleCommands(player)
        DataContainerManager.detachData(player, DataContainerType.PlayerSessionContainer)
    }

    fun updatePlayerSession(player: Player, newPlayerSession: PlayerSession) {
        val playerSessionData = ObjectSerializer.serializeToByteArray(newPlayerSession)

        CommandManager.updatePlayerAccessibleCommands(player)
        DataContainerManager.updateAttachedData(player, playerSessionData, DataContainerType.PlayerSessionContainer)
    }

    fun getPlayerSession(player: Player): PlayerSession? {
        val playerSessionData = DataContainerManager.getAttachedData(player, DataContainerType.PlayerSessionContainer)
            ?: return null

        return ObjectSerializer.deserialzeFromByteArray<PlayerSession>(playerSessionData)
    }

    fun assignPlayerRole(commander: CommandSender, targetName: String, roleCode: String) {
        val noPlayerProfileMsg = "${ChatColor.RED}No such player profile for '${ChatColor.WHITE}${targetName}${ChatColor.RED}'!"
        var displayMessage: String? = null

        getAllPlayerData().entries.find { it.value == targetName }
            ?.let { playerData ->
                val target = neon.server.getPlayer(playerData.key)
                val request = AssignRoleRequestDTO(playerData.key, roleCode)

                playerProfileAdapter.assignRole(CommandManager.getCommanderName(commander), request)
                    .onSuccess {
                        target?.let { targetPlayer ->
                            val playerSession = getPlayerSession(targetPlayer)
                                ?.copy(roleId = it.result!!) ?: return@onSuccess

                            updatePlayerSession(targetPlayer, playerSession)
                        }

                        displayMessage = "${ChatColor.GREEN}Role with role code '${ChatColor.WHITE}${roleCode}" +
                                "${ChatColor.GREEN}' has been assigned to ${ChatColor.WHITE}${playerData.value}${ChatColor.GREEN}!"
                    }
                    .onFailure {
                        displayMessage = "${ChatColor.RED}Error while trying to assign role to player! Please try again later!"

                        neon.server.logger.severe("Error while trying to assign role to player! Please try again later!")
                        throw it.neonException!!
                    }
                    .onOtherStatus {
                        displayMessage = when(it.status) {
                            ActionStatus.PLAYER_PROFILE_NOT_EXIST -> {
                                noPlayerProfileMsg
                            }

                            ActionStatus.PLAYER_ROLE_ALREADY_ASSIGN -> {
                                "${ChatColor.YELLOW}The target player already assigned with the given role!"
                            }

                            ActionStatus.ROLE_NOT_EXIST -> {
                                "${ChatColor.RED}No such role with role code as '${ChatColor.WHITE}" +
                                        "${roleCode}${ChatColor.RED}'!"
                            }

                            else -> return@onOtherStatus
                        }
                    }
            } ?: apply {
                displayMessage = noPlayerProfileMsg
            }

        displayMessage?.let {
            CommandSyntaxHandler.sendCommandSyntax(commander, displayMessage)
        }
    }

    fun unassignPlayerRole(commander: CommandSender, targetName: String) {
        val noPlayerProfileMsg = "${ChatColor.RED}No such player profile for '${ChatColor.WHITE}${targetName}"
        var displayMessage: String? = null

        getAllPlayerData().entries.find { it.value == targetName }
            ?.let { playerData ->
                val target = neon.server.getPlayer(playerData.key)
                val request = UnassignRoleRequestDTO(playerData.key)

                playerProfileAdapter.unassignRole(CommandManager.getCommanderName(commander), request)
                    .onSuccess {
                        target?.let { targetPlayer ->
                            val playerSession = getPlayerSession(targetPlayer)
                                ?.copy(roleId = null) ?: return@onSuccess

                            updatePlayerSession(targetPlayer, playerSession)
                        }

                        displayMessage = "${ChatColor.GREEN}Role has been unassigned from player '${ChatColor.WHITE}${playerData.value}" +
                                "${ChatColor.GREEN}'!"
                    }
                    .onFailure {
                        displayMessage = "${ChatColor.RED}Error while trying to unassign role from player! Please try again later!"

                        neon.server.logger.severe("Error while trying to unassign role from player! Please try again later!")
                        throw it.neonException!!
                    }
                    .onOtherStatus {
                        displayMessage = when(it.status) {
                            ActionStatus.PLAYER_PROFILE_NOT_EXIST -> {
                                noPlayerProfileMsg
                            }

                            ActionStatus.PLAYER_ROLE_NOT_ASSIGN -> {
                                "${ChatColor.RED}The target player has no role assigned!"
                            }

                            else -> return@onOtherStatus
                        }
                    }
            } ?: apply {
                displayMessage = noPlayerProfileMsg
            }

        displayMessage?.let {
            CommandSyntaxHandler.sendCommandSyntax(commander, displayMessage)
        }
    }

    fun getAllPlayerData(): HashMap<UUID, String> {
        val offlinePlayers = neon.server.offlinePlayers

        val playerData = HashMap<UUID, String>()

        offlinePlayers
            .filter { it.name != null }
            .forEach {
                playerData[it.uniqueId] = it.name!!
            }

        return playerData
    }

    private fun createPlayerProfile(player: Player) {
        var displayMessage: String? = null

        playerProfileAdapter.createPlayerProfile(
            CreatePlayerProfileRequestDTO(player.uniqueId, player.name)
        ).onSuccess {
            createPlayerSession(player, it.result!!)
        }.onFailure {
            displayMessage = "${ChatColor.RED}Error while trying to create player profile! Please try again later!"

            neon.server.logger.severe("Error while trying to create player profile! Please try again later!")
            throw it.neonException!!
        }.onOtherStatus {
            if (it.status != ActionStatus.PLAYER_PROFILE_EXIST) return@onOtherStatus

            if (appContext.serverRunningMode != ServerRunningMode.Online) return@onOtherStatus

            updatePlayerProfileName(player)?.let {
                createPlayerSession(player, it)
            }
        }

        displayMessage?.let {
            CommandSyntaxHandler.sendCommandSyntax(player, it)
        }
    }

    /**
     * Update player profile name when they join server with existing profile
     *
     * @param player
     */
    private fun updatePlayerProfileName(player: Player): PlayerProfileEntity? {
        val request = UpdatePlayerProfileRequestDTO(player.uniqueId, player.name)
        var playerProfile: PlayerProfileEntity? = null

        playerProfileAdapter.updatePlayerProfile(player.name, request)
            .onSuccess {
                playerProfile = it.result

                return@onSuccess
            }
            .onFailure {
                neon.server.logger.severe("Error while trying to create player profile! Please try again later!")
                throw it.neonException!!
            }

        return playerProfile
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