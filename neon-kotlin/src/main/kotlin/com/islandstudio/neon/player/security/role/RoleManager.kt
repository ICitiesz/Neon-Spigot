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
import com.islandstudio.neon.command.ICommandDispatcher
import com.islandstudio.neon.command.option.RoleCommandOption
import com.islandstudio.neon.command.processing.CommandSyntax
import com.islandstudio.neon.command.processing.CommandSyntaxHandler
import com.islandstudio.neon.command.properties.AccessibleCommand
import com.islandstudio.neon.player.session.PlayerSessionManager
import com.islandstudio.neon.shared.core.IRunner
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.exception.NeonException
import com.islandstudio.neon.shared.utils.TextUtil
import com.islandstudio.neon.stable.core.application.AppLoader
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.server.ServerLoadEvent
import org.bukkit.scoreboard.Scoreboard
import org.koin.core.annotation.Single
import org.koin.core.component.inject

@Single
class RoleManager: IComponentInjector {
    private val neon by inject<Neon>()
    private val roleAdapter by inject<RoleAdapter>()
    private lateinit var roleScoreboard: Scoreboard

    companion object: IRunner, ICommandDispatcher, IComponentInjector {
        private val roleCommandAlias = CommandAlias.RoleAlias
        private val roleManager by inject<RoleManager>()
        private val playerSessionManager by inject<PlayerSessionManager>()

        override fun run() {
            roleManager.initialize()

            AppLoader.registerEventProcessor(RoleManagerEvent())
        }

        override fun getCommandDispatcher(
            commander: CommandSender,
            playerAccessibleCommand: AccessibleCommand?,
            args: Array<out String>
        ) {
            val argLength = args.size.apply {
                if (this != 1) return@apply

                //TODO: GUI implementation
            }

            roleCommandAlias.onMatchOption(commander, args[1], playerAccessibleCommand) { commandOption ->
                when (commandOption) {
                    RoleCommandOption.Create -> {
                        if (!CommandAlias.validateCommandOptionArgLength(argLength, 4, 5)) {
                            return CommandSyntaxHandler.alertInvalidCommandArg(commander, args)
                        }

                        val roleCode = args[2].uppercase()
                        val roleDisplayName = args[3]

                        val underscoreAsSpace: Boolean = when (argLength) {
                            4 -> false
                            5 -> {
                                if (!RoleCommandOption.RoleCommandOptionArgument.CreateUnderscoreAsSpace.matchOptionArgument(
                                        commander,
                                        playerAccessibleCommand,
                                        args
                                    )
                                ) {
                                    return@onMatchOption
                                }

                                true
                            }

                            else -> return@onMatchOption CommandSyntaxHandler.alertInvalidCommandArg(commander, args)
                        }

                        roleManager.createRole(commander, roleCode, roleDisplayName, underscoreAsSpace)
                    }

                    RoleCommandOption.Remove -> {
                        if (!CommandAlias.validateCommandOptionArgLength(argLength, 3, 4)) {
                            return CommandSyntaxHandler.alertInvalidCommandArg(commander, args)
                        }

                        val argIndex = argLength - 1
                        val roleCode = args[2].uppercase()

                        when (argLength) {
                            3 -> {
                                return@onMatchOption CommandSyntaxHandler.sendCommandSyntax(
                                    commander, "${ChatColor.RED}Are you sure to remove the role by role code " +
                                            "'${ChatColor.WHITE}${roleCode}${ChatColor.RED}'? Upon removal, player with this role will be unassigned. To continue, please add '${ChatColor.WHITE}confirm" +
                                            "${ChatColor.RED}' at the end of the command."
                                )
                            }

                            4 -> {
                                if (!CommandAlias.validateConfirmation(args[argIndex])) {
                                    return@onMatchOption CommandSyntaxHandler.alertInvalidCommandArg(commander, args)
                                }

                                roleManager.removeRole(commander, roleCode)
                            }

                            else -> return@onMatchOption CommandSyntaxHandler.alertInvalidCommandArg(commander, args)
                        }
                    }

                    RoleCommandOption.Assign -> {
                        if (!CommandAlias.validateCommandOptionArgLength(argLength, 4)) {
                            return@onMatchOption CommandSyntaxHandler.alertInvalidCommandArg(commander, args)
                        }

                        val playerName = args[2]
                        val roleCode = args[3].uppercase()

                        playerSessionManager.assignPlayerRole(commander, playerName, roleCode)
                    }

                    RoleCommandOption.Unassign -> {
                        val argIndex = argLength - 1

                        if (!CommandAlias.validateCommandOptionArgLength(argLength, 3, 4)) {
                            return@onMatchOption CommandSyntaxHandler.alertInvalidCommandArg(commander, args)
                        }

                        val playerName = args[2]

                        when (argLength) {
                            3 -> {
                                return@onMatchOption CommandSyntaxHandler.sendCommandSyntax(
                                    commander, "${ChatColor.RED}Are you sure to unassign role from the player " +
                                            "'${ChatColor.WHITE}${playerName}${ChatColor.RED}'? Upon unassignment, player will not able to access certain feature based on permission that have " +
                                            "been granted before. To continue, please add '${ChatColor.WHITE}confirm${ChatColor.RED}' at the end of the command."
                                )
                            }

                            4 -> {
                                if (!CommandAlias.validateConfirmation(args[argIndex])) {
                                    return@onMatchOption CommandSyntaxHandler.alertInvalidCommandArg(commander, args)
                                }

                                playerSessionManager.unassignPlayerRole(commander, playerName)
                            }

                            else -> return@onMatchOption CommandSyntaxHandler.alertInvalidCommandArg(commander, args)
                        }
                    }

                    RoleCommandOption.Update -> {
                        roleCommandAlias.onMatchOption(commander, args[2], playerAccessibleCommand) { nestedCommandOption ->
                            when(nestedCommandOption) {
                                RoleCommandOption.RoleCode -> {
                                    if (!CommandAlias.validateCommandOptionArgLength(argLength, 5)) {
                                        return@onMatchOption CommandSyntaxHandler.alertInvalidCommandArg(commander, args)
                                    }

                                    val oldRoleCode = args[3].uppercase()
                                    val newRoleCode = args[4].uppercase()

                                    roleManager.updateRoleCode(commander, oldRoleCode, newRoleCode)
                                }

                                RoleCommandOption.RoleDisplayName -> {
                                    if (!CommandAlias.validateCommandOptionArgLength(argLength, 5, 6)) {
                                        return@onMatchOption CommandSyntaxHandler.alertInvalidCommandArg(commander, args)
                                    }

                                    val roleCode = args[3].uppercase()
                                    val newRoleDisplayName = args[4]

                                    val underscoreAsSpace: Boolean = when (argLength) {
                                        5 -> false
                                        6 -> {
                                            if (!RoleCommandOption.RoleCommandOptionArgument.UpdateUnderscoreAsSpace.matchOptionArgument(
                                                    commander,
                                                    playerAccessibleCommand,
                                                    args
                                                )
                                            ) {
                                                return@onMatchOption
                                            }

                                            true
                                        }

                                        else -> return@onMatchOption CommandSyntaxHandler.alertInvalidCommandArg(commander, args)
                                    }

                                    roleManager.updateRoleDisplayName(commander, roleCode, newRoleDisplayName, underscoreAsSpace)
                                }

                                else -> return CommandSyntaxHandler.alertInvalidCommandArg(commander, args)
                            }
                        }
                    }

                    else -> CommandSyntaxHandler.alertInvalidCommandArg(commander, args)
                }
            }
        }

        override fun getTabCompletion(
            commander: CommandSender,
            playerAccessibleCommand: AccessibleCommand?,
            args: Array<out String>
        ): MutableList<String> {
            return when(val argLength = args.size) {
                2 -> {
                    val argIndex = argLength - 1

                    CommandAlias.getAccessibleCommandOptions(
                        commander,
                        roleCommandAlias,
                        playerAccessibleCommand,
                        args[argIndex]
                    )
                }

                3 -> {
                    val argIndex = argLength - 1

                    roleCommandAlias.onMatchOption(commander, args[1], playerAccessibleCommand) { roleCommandOption ->
                        when (roleCommandOption) {
                            RoleCommandOption.Remove -> {
                                CommandAlias.getTabCompleteSuggestion(
                                    roleManager.getAllRole(),
                                    args[argIndex]
                                ) { roleEntities ->
                                    roleEntities
                                        .filter { x -> !x.roleCode.isNullOrEmpty() }
                                        .map { x -> x.roleCode!! }
                                }
                            }

                            RoleCommandOption.Assign -> CommandAlias.getTabCompleteSuggestion(
                                playerSessionManager.getAllPlayerData().values, args[argIndex]
                            ) { it }

                            RoleCommandOption.Unassign -> CommandAlias.getTabCompleteSuggestion(
                                playerSessionManager.getAllPlayerData().values, args[argIndex]
                            ) { it }

                            RoleCommandOption.Update -> CommandAlias.getAccessibleCommandOptions(
                                commander,
                                roleCommandAlias,
                                playerAccessibleCommand,
                                args[argIndex],
                                2
                            )

                            else -> super.getTabCompletion(commander, playerAccessibleCommand, args)
                        }
                    }
                }

                4 -> {
                    val argIndex = argLength - 1

                    roleCommandAlias.onMatchOption(commander, args[1], playerAccessibleCommand) { commandOption ->
                        when (commandOption) {
                            RoleCommandOption.Assign -> CommandAlias.getTabCompleteSuggestion(
                                roleManager.getAllRole(),
                                args[argIndex]
                            ) { roleEntities ->
                                roleEntities
                                    .filter { x -> !x.roleCode.isNullOrEmpty() }
                                    .map { x -> x.roleCode!! }
                            }

                            else -> return@onMatchOption
                        }
                    }

                    roleCommandAlias.onMatchOption(commander, args[2], playerAccessibleCommand) { commandOption ->
                        when(commandOption) {
                            RoleCommandOption.RoleCode -> CommandAlias.getTabCompleteSuggestion(
                                roleManager.getAllRole(),
                                args[argIndex]
                            ) { roleEntities ->
                                roleEntities
                                    .filter { x -> !x.roleCode.isNullOrEmpty() }
                                    .map { x -> x.roleCode!! }
                            }

                            RoleCommandOption.RoleDisplayName -> CommandAlias.getTabCompleteSuggestion(
                                roleManager.getAllRole(),
                                args[argIndex]
                            ) { roleEntities ->
                                roleEntities
                                    .filter { x -> !x.roleCode.isNullOrEmpty() }
                                    .map { x -> x.roleCode!! }
                            }

                            else -> super.getTabCompletion(commander, playerAccessibleCommand, args)
                        }
                    }
                }

                5 -> {
                    val argIndex = argLength - 1

                    roleCommandAlias.onMatchOption(commander, args[1], playerAccessibleCommand) { commandOption ->
                        when(commandOption) {
                            RoleCommandOption.Create -> {
                                CommandAlias.getAccessibleCommandOptionArgs(
                                    commander,
                                    args[argIndex],
                                    commandOption,
                                    playerAccessibleCommand
                                )
                            }

                            else -> super.getTabCompletion(commander, playerAccessibleCommand, args)
                        }
                    }
                }

                6 -> {
                    roleCommandAlias.onMatchOption(commander, args[2], playerAccessibleCommand) { commandOption ->
                        when(commandOption) {
                            RoleCommandOption.RoleDisplayName -> CommandAlias.getAccessibleCommandOptionArgs(
                                commander,
                                args[argLength - 1],
                                commandOption,
                                playerAccessibleCommand
                            )

                            else -> super.getTabCompletion(commander, playerAccessibleCommand, args)
                        }
                    }
                }

                else -> super.getTabCompletion(commander, playerAccessibleCommand, args)
            }
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