package com.islandstudio.neon.player.security.role

import com.islandstudio.neon.Neon
import com.islandstudio.neon.api.adapter.security.RoleAdapter
import com.islandstudio.neon.api.dto.action.ActionStatus
import com.islandstudio.neon.api.dto.request.security.CreateRoleRequestDTO
import com.islandstudio.neon.api.dto.request.security.role.GetRoleRequestDTO
import com.islandstudio.neon.api.dto.request.security.role.RemoveRoleRequestDTO
import com.islandstudio.neon.api.dto.request.security.role.UpdateRoleRequestDTO
import com.islandstudio.neon.api.entity.security.RoleEntity
import com.islandstudio.neon.command.CommandAlias
import com.islandstudio.neon.command.CommandManager
import com.islandstudio.neon.command.processing.CommandSyntax
import com.islandstudio.neon.command.processing.CommandSyntaxHandler
import com.islandstudio.neon.core.initialization.NeonPluginLoader
import com.islandstudio.neon.player.session.PlayerSessionManager
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.exception.NeonException
import com.islandstudio.neon.shared.core.initialization.IRunner
import com.islandstudio.neon.shared.utils.TextUtil
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.server.ServerLoadEvent
import org.bukkit.scoreboard.Scoreboard
import org.koin.core.component.inject

//@Single
class RoleManager: IComponentInjector {
    private val neon by inject<Neon>()
    private val roleAdapter by inject<RoleAdapter>()
    private lateinit var roleScoreboard: Scoreboard

    companion object: IRunner, IComponentInjector {
        private val roleCommandAlias = CommandAlias.RoleAlias
        private val roleManager by inject<RoleManager>()
        private val playerSessionManager by inject<PlayerSessionManager>()

        override fun run() {
            roleManager.initialize()

            NeonPluginLoader.registerEventProcessor(RoleManagerEvent())
        }
    }

    fun initialize() {
        roleScoreboard = neon.server.scoreboardManager?.newScoreboard
            ?: throw NeonException("Could not initialize role scoreboard due to world not loaded!")

        roleManager.getAllRole().forEach {
            if (!addRoleToScoreboard(it)) return@forEach
        }
    }

    fun addRoleToScoreboard(role: RoleEntity): Boolean {
        val roleCode = role.roleCode.run {
            if (this.isNullOrEmpty()) return false

            this
        }

        val roleDisplayName = role.roleDisplayName.run {
            if (this.isNullOrEmpty()) return false

            this
        }

        if (roleScoreboard.teams.any { x -> x.name == roleCode }) return false

        roleScoreboard.registerNewTeam(roleCode).apply {
            this.prefix = "${TextUtil.toColorText(roleDisplayName)} "
        }

        return true
    }

    fun removeRoleFromScoreboard(roleCode: String) {
        roleScoreboard.getTeam(roleCode)?.unregister()
    }

    fun removeRoleTag(player: Player) {
        val playerSession = playerSessionManager.getPlayerSession(player) ?: return
        val playerRole = getRole(null, playerSession.roleId) ?: return

        roleScoreboard.getTeam(playerRole.roleCode!!)?.removeEntry(player.name)

        player.scoreboard = roleScoreboard
    }

    fun addRoleTag(player: Player) {
        val playerSession = playerSessionManager.getPlayerSession(player) ?: return
        val playerRole = getRole(null, playerSession.roleId) ?: return

        roleScoreboard.getTeam(playerRole.roleCode!!)?.addEntry(player.name)

        player.scoreboard = roleScoreboard
    }

    /**
     * Create custom role.
     *
     * @param commander The commander who perform the command
     * @param roleCode Secondary identifier for role
     * @param roleDisplayName Role name that display beside player name.
     * @param underscoreAsSpace Replace underscore with space if true
     */
    fun createRole(commander: CommandSender, roleCode: String, roleDisplayName: String, underscoreAsSpace: Boolean = false) {
        val modifiedDisplayName = if (underscoreAsSpace) roleDisplayName.replace('_', ' ') else roleDisplayName
        val request = CreateRoleRequestDTO(TextUtil.toColorText(modifiedDisplayName), roleCode)
        var displayMessage: String? = null

        roleAdapter.createRole(CommandManager.getCommanderName(commander), request)
            .onSuccess {
                addRoleToScoreboard(it.result!!)

                displayMessage = "${ChatColor.GREEN}The role has been created!"
            }
            .onFailure {
                displayMessage = "${ChatColor.RED}Error while trying to create role! Please try again later!"

                neon.server.logger.severe("Error while trying to create role! Please try again later!")
                throw it.neonException!!
            }
            .onOtherStatus {
                displayMessage = when(it.status) {
                    ActionStatus.INVALID_REQUEST_FIELD -> {
                        CommandSyntax.INVALID_REQUEST_FIELD.syntax
                    }

                    ActionStatus.ROLE_EXIST -> {
                        "${ChatColor.YELLOW}The role with role code '${ChatColor.WHITE}${roleCode}" +
                                "${ChatColor.YELLOW}' already exists!"
                    }

                    else -> return@onOtherStatus
                }
            }

        displayMessage?.let {
            CommandSyntaxHandler.sendCommandSyntax(commander, it)
        }
    }

    fun updateRoleCode(commander: CommandSender, oldRoleCode: String, newRoleCode: String) {
        val role = getRole(commander, roleCode = oldRoleCode) ?: return

        val request = UpdateRoleRequestDTO(role.roleId!!, newRoleCode.uppercase(), role.roleDisplayName!!)
        var displayMessage: String? = null

        roleAdapter.updateRole(CommandManager.getCommanderName(commander), request)
            .onSuccess {
                val playerEntries = roleScoreboard.getTeam(oldRoleCode)?.entries

                it.result?.let { x ->
                    if (!addRoleToScoreboard(x)) return@let

                    val roleTeam = roleScoreboard.getTeam(x.roleCode!!)

                    playerEntries?.let { entries ->
                        entries.forEach { entry ->
                            roleTeam?.addEntry(entry)
                        }
                    }
                }

                removeRoleFromScoreboard(oldRoleCode)

                displayMessage = "${ChatColor.GREEN}The role has been updated!"
            }
            .onFailure {
                displayMessage = "${ChatColor.RED}Error while trying to update role! Please try again later!"

                neon.server.logger.severe("Error while trying to update role! Please try again later!")
                throw it.neonException!!
            }
            .onOtherStatus {
                displayMessage = when(it.status) {
                    ActionStatus.ROLE_EXIST -> {
                        "${ChatColor.YELLOW}The new role code '${ChatColor.WHITE}${newRoleCode.uppercase()}" +
                                "${ChatColor.YELLOW}' already exists!"
                    }

                    ActionStatus.ROLE_NOT_EXIST -> {
                        "${ChatColor.RED}The given role not exist"
                    }

                    else -> return@onOtherStatus
                }
            }

        displayMessage?.let {
            CommandSyntaxHandler.sendCommandSyntax(commander, it)
        }
    }

    fun updateRoleDisplayName(commander: CommandSender, roleCode: String, newRoleDisplayName: String, underscoreAsSpace: Boolean = false) {
        val role = getRole(commander, roleCode = roleCode) ?: return

        val updatedRoleDisplayName = if (underscoreAsSpace) newRoleDisplayName.replace('_', ' ') else newRoleDisplayName
        val request = UpdateRoleRequestDTO(role.roleId!!, null, updatedRoleDisplayName)
        var displayMessage: String? = null

        roleAdapter.updateRole(CommandManager.getCommanderName(commander), request)
            .onSuccess {
                it.result?.let {
                    roleScoreboard.getTeam(roleCode)?.let { roleTeam ->
                        roleTeam.prefix = "${TextUtil.toColorText(it.roleDisplayName!!)} "
                    }
                }

                displayMessage = "${ChatColor.GREEN}The role has been updated!"
            }
            .onFailure {
                displayMessage = "${ChatColor.RED}Error while trying to update role! Please try again later!"

                neon.server.logger.severe("Error while trying to update role! Please try again later!")
                throw it.neonException!!
            }
            .onOtherStatus {
                displayMessage = when(it.status) {
                    ActionStatus.ROLE_NOT_EXIST -> {
                        "${ChatColor.RED}The given role not exist"
                    }

                    else -> return@onOtherStatus
                }
            }

        displayMessage?.let {
            CommandSyntaxHandler.sendCommandSyntax(commander, it)
        }
    }

    /**
     * Remove role
     *
     * @param commander The commander who perform the command
     * @param roleCode Secondary identifier for the role
     */
    fun removeRole(commander: CommandSender?, roleCode: String) {
        val request = RemoveRoleRequestDTO(roleCode)
        var displayMessage: String? = null

        roleAdapter.removeRole(request)
            .onSuccess {
                if (it.result!! > 0) {
                    removeRoleFromScoreboard(roleCode.uppercase())

                    displayMessage = "${ChatColor.GREEN}Role with role code '${ChatColor.WHITE}$roleCode" +
                            "${ChatColor.GREEN}' has been removed!"
                    return@onSuccess
                }

                displayMessage = "${ChatColor.YELLOW}No such role found! May be already removed?"
            }
            .onFailure {
                displayMessage = "${ChatColor.RED}Error while trying to remove role! Please try again later!"

                neon.server.logger.severe("Error while trying to remove role! Please try again later!")
                throw it.neonException!!
            }

        if (!(commander != null && displayMessage != null)) return

        CommandSyntaxHandler.sendCommandSyntax(commander, displayMessage)
    }

    /**
     * Get role by either roldId or roleCode
     *
     * @param commander The commander who perform the command
     * @param roleId
     * @param roleCode
     * @return
     */
    fun getRole(commander: CommandSender?, roleId: Long? = null, roleCode: String? = null): RoleEntity? {
        val request = GetRoleRequestDTO(roleId, roleCode)
        var displayMessage: String? = null
        var role: RoleEntity? = null

        roleAdapter.getRole(request)
            .onSuccess {
                it.result?.let { role = it }
            }
            .onFailure {
                displayMessage = "${ChatColor.RED}Error while trying to get role! Please try again later!"

                neon.server.logger.severe("Error while trying to get role! Please try again later!")
                throw it.neonException!!
            }
            .onOtherStatus {
                displayMessage = when(it.status) {
                    ActionStatus.INVALID_REQUEST_FIELD -> {
                        CommandSyntax.INVALID_REQUEST_FIELD.syntax
                    }

                    ActionStatus.ROLE_NOT_EXIST -> {
                        "${ChatColor.RED}No such role with role code as '${ChatColor.WHITE}" +
                                "${roleCode}${ChatColor.RED}'!"
                    }

                    else -> return@onOtherStatus Unit
                }
            }

        if (commander != null && displayMessage != null){
            CommandSyntaxHandler.sendCommandSyntax(commander, displayMessage)
        }

        return role
    }

    fun getAllRole(): ArrayList<RoleEntity> {
        val roleList = ArrayList<RoleEntity>()

        roleAdapter.getAllRole()
            .onSuccess {
                roleList.addAll(it.result!!.roleList)
            }
            .onFailure {
                neon.server.logger.severe("Error while trying to get role! Please try again later!")
                throw it.neonException!!
            }

        return roleList
    }

    fun attachRoleTagToChat(player: Player): String {
        var chatMsgPrefix = "${ChatColor.WHITE}${player.name} > ${ChatColor.WHITE}%2\$s"

        val playerSession = playerSessionManager.getPlayerSession(player) ?: return chatMsgPrefix
        val playerRole = getRole(null, playerSession.roleId) ?: return chatMsgPrefix

        playerRole.roleDisplayName?.let {
            chatMsgPrefix = "${TextUtil.toColorText(it)} $chatMsgPrefix"
        }

        return chatMsgPrefix
    }

    private class RoleManagerEvent: Listener, IComponentInjector {
        private val neon by inject<Neon>()
        private val roleManager by inject<RoleManager>()

        @EventHandler
        private fun onPlayerChat(e: AsyncPlayerChatEvent) {
            e.format = roleManager.attachRoleTagToChat(e.player)
        }

        @EventHandler
        private fun onServerLoad(e: ServerLoadEvent) {
            if (e.type != ServerLoadEvent.LoadType.RELOAD) return

            neon.server.onlinePlayers.forEach {
                roleManager.addRoleTag(it)
            }
        }
    }
}