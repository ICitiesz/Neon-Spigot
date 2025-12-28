package com.islandstudio.neon.player.role

import com.islandstudio.neon.apinew.dto.action.player.role.CreatePlayerRoleActionDTO
import com.islandstudio.neon.apinew.dto.action.player.role.UpdatePlayerRoleActionDTO
import com.islandstudio.neon.apinew.entity.player.PlayerRole
import com.islandstudio.neon.apinew.facade.player.IPlayerProfileFacade
import com.islandstudio.neon.apinew.facade.player.IPlayerRoleFacade
import com.islandstudio.neon.apinew.status.ActionResultStatus
import com.islandstudio.neon.command.processing.CommandSyntaxHandler
import com.islandstudio.neon.player.session.PlayerSessionManagerNew
import com.islandstudio.neon.shared.core.di.IComponentProvider
import com.islandstudio.neon.shared.core.di.getComponent
import com.islandstudio.neon.shared.core.di.injectComponent
import com.islandstudio.neon.shared.core.exception.NeonException
import com.islandstudio.neon.shared.core.initialization.IPluginContext
import com.islandstudio.neon.shared.core.initialization.IRunnerNew
import com.islandstudio.neon.shared.experimental.utils.coroutines.CloseableCoroutineScope
import com.islandstudio.neon.shared.utils.TextUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.ServerLoadEvent
import org.bukkit.scoreboard.Scoreboard
import kotlin.jvm.optionals.getOrNull

class RoleManagerNew: IRunnerNew, IComponentProvider {
    private val pluginContext = getComponent<IPluginContext>()
    private val playerSessionManager by injectComponent<PlayerSessionManagerNew>()
    private var roleScoreboard: Scoreboard? = null

    val commandDispatcher = RoleCommandDispatcher(this)

    init {
        getKoin().declare(this)
    }

    override fun run() {
        roleScoreboard = pluginContext.getServer().scoreboardManager?.newScoreboard
            ?: throw NeonException("Could not initialize role scoreboard due to world not loaded!")

        CloseableCoroutineScope(Dispatchers.IO).launchJob {
            val allRoles = async {
                getAllRole()
            }.await()

            allRoles.forEach { role ->
                if (!addRoleToScoreboard(role)) return@forEach
            }
        }

        registerEvent(RoleManagerEvent(this))
    }

    suspend fun createRole(commander: CommandSender, code: String, displayName: String, underscoreAsSpace: Boolean = false) {
        val playerRoleFacade = getComponent<IPlayerRoleFacade>(null) // TODO: Need handle user context
        val updatedDisplayName = if (underscoreAsSpace) displayName.replace('_', ' ') else displayName

        var actionMessage = "The player role has been created!"

        playerRoleFacade.createPlayerRole(CreatePlayerRoleActionDTO(updatedDisplayName, code)).getResult { status, _ ->
            actionMessage = when (status) {
                is ActionResultStatus.PlayerRoleAlreadyExist -> {
                    status.message
                }

                else -> {
                    "Can't create player role!"
                }
            }
        }

        CommandSyntaxHandler.sendCommandSyntax(commander, actionMessage)
    }

    suspend fun removeRole(commander: CommandSender, code: String) {
        val playerRoleFacade = getComponent<IPlayerRoleFacade>(null)
        var actionMessage = "The player role has been removed!"

        playerRoleFacade.removePlayerRole(code).getResult { status, _ ->
            actionMessage = when (status) {
                is ActionResultStatus.PlayerRoleNotExist -> {
                    status.message
                }

                else -> {
                    "Can't remove player role!"
                }
            }
        }

        CommandSyntaxHandler.sendCommandSyntax(commander, actionMessage)
    }

    @ExperimentalStdlibApi
    suspend fun getRole(roleId: Long?, roleCode: String?): PlayerRole? {
        val playerRoleFacade = getComponent<IPlayerRoleFacade>(null)

        return playerRoleFacade.getPlayerRole(roleId, roleCode).getResult{ status, _ ->
            pluginContext.getPluginLogger().warning(status.message)
        }.getOrNull()
    }

    suspend fun getAllRole(): ArrayList<PlayerRole> {
        val playerRoleFacade = getComponent<IPlayerRoleFacade>(null)

        return playerRoleFacade.getAllPlayerRoles().getResult().get()
    }

    suspend fun updateRoleDisplayName(commander: CommandSender, roleCode: String, newDisplayName: String, underscoreAsSpace: Boolean = false) {
        val playerRoleFacade = getComponent<IPlayerRoleFacade>(commander)
        val updatedDisplayName = if (underscoreAsSpace) newDisplayName.replace('_', ' ') else newDisplayName
        var actionMessage = "The player role has been updated!"

        playerRoleFacade.updatePlayerRole(UpdatePlayerRoleActionDTO(roleCode, updatedDisplayName, null)).getResult { status, _ ->
            actionMessage = when (status) {
                is ActionResultStatus.PlayerRoleNotExist -> {
                    status.message
                }

                else -> {
                    "Can't update role display name!"
                }
            }
        }

        CommandSyntaxHandler.sendCommandSyntax(commander, actionMessage)
    }

    suspend fun updateRoleCode(commander: CommandSender, oldRoleCode: String, newRoleCode: String) {
        val playerRoleFacade = getComponent<IPlayerRoleFacade>(commander)
        var actionMessage = "The player role has been updated!"

        playerRoleFacade.updatePlayerRole(UpdatePlayerRoleActionDTO(oldRoleCode, null,
            newRoleCode
        )).getResult(onSuccess = {
            val result = this.get()
            val playerEntries = roleScoreboard?.getTeam(oldRoleCode)?.entries

            if (!addRoleToScoreboard(result)) return

            val roleTeam = roleScoreboard?.getTeam(result.code)

            playerEntries?.let { entires ->
                entires.forEach {
                    roleTeam?.addEntry(it)
                }
            }

            removeRoleFromScoreboard(oldRoleCode)

            return@getResult this
        }, onFailure = { status, _ ->
            actionMessage = when (status) {
                is ActionResultStatus.PlayerRoleNotExist -> {
                    status.message
                }

                else -> {
                    "Can't update role code!"
                }
            }
        })

        CommandSyntaxHandler.sendCommandSyntax(commander, actionMessage)
    }

    suspend fun assignPlayerRole(commander: CommandSender, targetPlayerName: String, roleCode: String) {
        val playerProfileFacade = getComponent<IPlayerProfileFacade>() // TODO: Need handle user context

        playerSessionManager.getAllPlayers().find { it.name == targetPlayerName }
            ?.let {
                val result = playerProfileFacade.assignPlayerRole(it.uniqueId, roleCode).getResult().get()

                if (!it.isOnline) return

                val playerSession = playerSessionManager.getPlayerSession(it)?.copy(roleId = result.roleId) ?: return
                playerSessionManager.updatePlayerSession(it, playerSession)
                // TODO: update player role tag
            }
    }

    suspend fun unassignPlayerRole(commander: CommandSender, targetPlayerName: String) {
        val playerProfileFacade = getComponent<IPlayerProfileFacade>() // TODO: Need handle user context

        playerSessionManager.getAllPlayers().find { it.name == targetPlayerName }
            ?.let {
                playerProfileFacade.unassignPlayerRole(it.uniqueId).getResult(onSuccess = {
                    removeRoleTag(it)
                    return@getResult this
                })

                if (!it.isOnline) return

                val playerSession = playerSessionManager.getPlayerSession(it)?.copy(roleId = null) ?: return
                playerSessionManager.updatePlayerSession(it, playerSession)
            }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun attachRoleTagChat(player: Player): String {
        var chatMsgPrefix = "${ChatColor.WHITE}${player.name} > ${ChatColor.WHITE}%2\$s"
        val playerSession = playerSessionManager.getPlayerSession(player) ?: return chatMsgPrefix

        chatMsgPrefix = CloseableCoroutineScope(Dispatchers.IO).launchAsCompletableDeferredResult {
            val playerRole = async {
                getRole(playerSession.roleId, null)
            }.await() ?: return@launchAsCompletableDeferredResult chatMsgPrefix

            "${TextUtil.toColorText(playerRole.displayName)} $chatMsgPrefix"
        }.asCompletableFuture().get()

        return chatMsgPrefix
    }

    private fun addRoleToScoreboard(playerRole: PlayerRole): Boolean {
        val roleCode = playerRole.code.also {
            if (it.isEmpty()) return false
        }

        val roleDisplayName = playerRole.displayName.also {
            if (it.isEmpty()) return false
        }

        roleScoreboard?.let {
            if (it.teams.any { x -> x.name == roleCode }) return false

            it.registerNewTeam(roleCode).apply {
                this.prefix = "${TextUtil.toColorText(roleDisplayName)} "
            }

            return true
        }
        return false
    }

    private fun removeRoleFromScoreboard(roleCode: String) {
        roleScoreboard?.let {
            it.getTeam(roleCode)?.unregister()
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun addRoleTag(player: Player) {
        val playerSession = playerSessionManager.getPlayerSession(player) ?: return

        CloseableCoroutineScope(Dispatchers.IO).launchJob {
            val playerRole = async {
                getRole(playerSession.roleId, null)
            }.await() ?: return@launchJob

            roleScoreboard?.let {
                it.getTeam(playerRole.code)?.addEntry(player.name)

                player.scoreboard = it
            }
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun removeRoleTag(player: Player) {
        val playerSession = playerSessionManager.getPlayerSession(player) ?: return

        CloseableCoroutineScope(Dispatchers.IO).launchJob {
            val playerRole = async {
                getRole(playerSession.roleId, null)
            }.await() ?: return@launchJob

            roleScoreboard?.let {
                it.getTeam(playerRole.code)?.removeEntry(player.name)

                player.scoreboard = it
            }
        }
    }

    private class RoleManagerEvent(private val roleManager: RoleManagerNew): Listener, IComponentProvider {
        private val pluginContext = getComponent<IPluginContext>()

        @EventHandler
        private fun onPlayerChat(e: AsyncPlayerChatEvent) {
            e.format = roleManager.attachRoleTagChat(e.player)
        }

        @EventHandler
        private fun onServerLoad(e: ServerLoadEvent) {
            if (e.type != ServerLoadEvent.LoadType.RELOAD) return

            pluginContext.getServer().onlinePlayers.forEach {
                roleManager.addRoleTag(it)
            }
        }

        @EventHandler
        private fun onPlayerJoin(e: PlayerJoinEvent) {
            roleManager.addRoleTag(e.player)
        }

        @EventHandler
        private fun onPlayerQuit(e: PlayerQuitEvent) {
            roleManager.removeRoleTag(e.player)
        }
    }
}