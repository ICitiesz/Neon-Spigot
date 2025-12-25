package com.islandstudio.neon.player.role

import com.islandstudio.neon.apinew.dto.action.player.role.CreatePlayerRoleActionDTO
import com.islandstudio.neon.apinew.dto.action.player.role.UpdatePlayerRoleActionDTO
import com.islandstudio.neon.apinew.entity.player.PlayerRole
import com.islandstudio.neon.apinew.facade.player.IPlayerRoleFacade
import com.islandstudio.neon.apinew.status.ActionResultStatus
import com.islandstudio.neon.command.ICommandDispatcherNew
import com.islandstudio.neon.command.processing.CommandSyntaxHandler
import com.islandstudio.neon.shared.core.di.IComponentProvider
import com.islandstudio.neon.shared.core.di.getComponent
import com.islandstudio.neon.shared.core.exception.NeonException
import com.islandstudio.neon.shared.core.initialization.IPluginContext
import com.islandstudio.neon.shared.core.initialization.IRunnerNew
import org.bukkit.command.CommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.server.ServerLoadEvent
import org.bukkit.scoreboard.Scoreboard
import kotlin.jvm.optionals.getOrNull

class RoleManagerNew: IRunnerNew, IComponentProvider {
    private val pluginContext = getComponent<IPluginContext>()
    private var roleScoreboard: Scoreboard? = null

    val roleManagerCommandDispatcher: ICommandDispatcherNew = RoleManagerCommandDispatcher(this)

    init {
        getKoin().declare(this)
    }

    override fun run() {
        roleScoreboard = pluginContext.getServer().scoreboardManager?.newScoreboard
            ?: throw NeonException("Could not initialize role scoreboard due to world not loaded!")

        // get all role and register to the scoreboard

        //registerEvent(RoleManagerEvent(this))
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
    suspend fun getRole(code: String): PlayerRole? {
        val playerRoleFacade = getComponent<IPlayerRoleFacade>(null)

        return playerRoleFacade.getPlayerRole(code).getResult{ status, _ ->
            pluginContext.getPluginLogger().warning(status.message)
        }.getOrNull()
    }

    suspend fun updateRoleDisplayName(commander: CommandSender, code: String, newDisplayName: String, underscoreAsSpace: Boolean = false) {
        val playerRoleFacade = getComponent<IPlayerRoleFacade>(commander)
        val updatedDisplayName = if (underscoreAsSpace) newDisplayName.replace('_', ' ') else newDisplayName
        var actionMessage = "The player role has been updated!"

        playerRoleFacade.updatePlayerRole(UpdatePlayerRoleActionDTO(code, updatedDisplayName, null)).getResult { status, _ ->
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

    suspend fun updateRoleCode(commander: CommandSender, code: String, newCode: String) {
        val playerRoleFacade = getComponent<IPlayerRoleFacade>(commander)
        var actionMessage = "The player role has been updated!"

        playerRoleFacade.updatePlayerRole(UpdatePlayerRoleActionDTO(code, null, newCode)).getResult { status, _ ->
            actionMessage = when (status) {
                is ActionResultStatus.PlayerRoleNotExist -> {
                    status.message
                }

                else -> {
                    "Can't update role code!"
                }
            }
        }

        CommandSyntaxHandler.sendCommandSyntax(commander, actionMessage)
    }

    suspend fun getAllRole(): ArrayList<PlayerRole> {
        val playerRoleFacade = getComponent<IPlayerRoleFacade>(null)

        return playerRoleFacade.getAllPlayerRoles().getResult().get()
    }

    private class RoleManagerEvent(roleManagerNew: RoleManagerNew): Listener {
        @EventHandler
        private fun onPlayerChat(e: AsyncPlayerChatEvent) {

        }

        @EventHandler
        private fun onServerLoad(e: ServerLoadEvent) {
            if (e.type != ServerLoadEvent.LoadType.RELOAD) return
        }
    }
}