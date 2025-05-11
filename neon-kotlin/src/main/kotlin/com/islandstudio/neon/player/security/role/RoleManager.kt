package com.islandstudio.neon.player.security.role

import com.islandstudio.neon.Neon
import com.islandstudio.neon.api.adapter.security.RoleAdapter
import com.islandstudio.neon.api.dto.action.ActionStatus
import com.islandstudio.neon.api.dto.request.security.CreateRoleRequestDTO
import com.islandstudio.neon.api.dto.request.security.role.GetRoleRequestDTO
import com.islandstudio.neon.api.dto.request.security.role.RemoveRoleRequestDTO
import com.islandstudio.neon.api.entity.security.RoleEntity
import com.islandstudio.neon.command.CommandAlias
import com.islandstudio.neon.command.CommandManager
import com.islandstudio.neon.command.ICommandDispatcher
import com.islandstudio.neon.command.option.RoleCommandOption
import com.islandstudio.neon.command.processing.CommandSyntax
import com.islandstudio.neon.command.processing.CommandSyntaxHandler
import com.islandstudio.neon.command.properties.AccessibleCommand
import com.islandstudio.neon.player.session.PlayerSessionManager
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.utils.TextUtil
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.koin.core.annotation.Single
import org.koin.core.component.inject

@Single
class RoleManager: IComponentInjector {
    private val neon by inject<Neon>()
    private val roleAdapter by inject<RoleAdapter>()

    companion object: ICommandDispatcher, IComponentInjector {
        private val roleCommandAlias = CommandAlias.RoleAlias
        private val roleManager by inject<RoleManager>()
        private val playerSessionManager by inject<PlayerSessionManager>()

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
                                if (!RoleCommandOption.RoleCommandOptionArgument.UnderscoreAsSpace.matchOptionArgument(
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
                        args[argIndex],
                        roleCommandAlias,
                        playerAccessibleCommand
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

                            else -> super.getTabCompletion(commander, playerAccessibleCommand, args)
                        }
                    }
                }

                4 -> {
                    val argIndex = argLength - 1

                    roleCommandAlias.onMatchOption(commander, args[1], playerAccessibleCommand) { roleCommandOption ->
                        when (roleCommandOption) {
                            RoleCommandOption.Assign -> CommandAlias.getTabCompleteSuggestion(
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

                    roleCommandAlias.onMatchOption(
                        commander,
                        args[argIndex],
                        playerAccessibleCommand
                    ) { roleCommandOption ->
                        when (roleCommandOption) {
                            RoleCommandOption.Create -> {
                                CommandAlias.getAccessibleCommandOptionArgs(
                                    commander,
                                    args[argIndex],
                                    roleCommandAlias,
                                    playerAccessibleCommand
                                )
                            }

                            else -> super.getTabCompletion(commander, playerAccessibleCommand, args)
                        }
                    }
                }

                else -> super.getTabCompletion(commander, playerAccessibleCommand, args)
            }
        }
    }

    /**
     * Create custom role.
     *
     * @param commander The commander who perform the command
     * @param roleCode Readable identifier for role
     * @param roleDisplayName Role name that display beside player name.
     * @param underscoreAsSpace Replace underscore with space if true
     */
    fun createRole(commander: CommandSender, roleCode: String, roleDisplayName: String, underscoreAsSpace: Boolean = false) {
        val modifiedDisplayName = if (underscoreAsSpace) roleDisplayName.replace('_', ' ') else roleDisplayName
        val request = CreateRoleRequestDTO(TextUtil.toColorText(modifiedDisplayName), roleCode)
        var displayMessage: String? = null

        roleAdapter.createRole(CommandManager.getCommanderName(commander), request)
            .onSuccess {
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

    fun removeRole(commander: CommandSender?, roleCode: String) {
        val request = RemoveRoleRequestDTO(roleCode)
        var displayMessage: String? = null

        roleAdapter.removeRole(request)
            .onSuccess {
                displayMessage = if (it.result!! > 0) "${ChatColor.GREEN}Role with role code '${ChatColor.WHITE}$roleCode" +
                        "${ChatColor.GREEN}' has been removed!"
                else "${ChatColor.YELLOW}No such role found! May be already removed?"
            }
            .onFailure {
                displayMessage = "${ChatColor.RED}Error while trying to remove role! Please try again later!"

                neon.server.logger.severe("Error while trying to remove role! Please try again later!")
                throw it.neonException!!
            }

        if (!(commander != null && displayMessage != null)) return

        CommandSyntaxHandler.sendCommandSyntax(commander, displayMessage)
    }

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
}