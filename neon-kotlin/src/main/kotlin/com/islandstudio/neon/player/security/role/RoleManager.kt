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
import com.islandstudio.neon.command.properties.CommandFilter
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
            accessibleCommands: ArrayList<AccessibleCommand>,
            args: Array<out String>
        ) {
            val argLength = args.size.apply {
                if (this != 1) return@apply

                //TODO: GUI implementation
            }

            val accessibleCommandOptions = CommandAlias.getAccessibleCommandOptions(
                commander,
                accessibleCommands,
                roleCommandAlias
            )

            roleCommandAlias.onMatchOption(args[1]) {
                it?.let {
                    if (it.option !in accessibleCommandOptions) {
                        return@onMatchOption CommandSyntaxHandler.sendCommandSyntax(commander, CommandSyntax.INVALID_PERMISSION)
                    }
                }

                when(it) {
                    RoleCommandOption.Create -> {
                        if (!(argLength == 4 || argLength == 5)) {
                            return@onMatchOption CommandSyntaxHandler.alertInvalidCommandArg(commander, args, argLength - 1)
                        }

                        val roleCode = args[2].uppercase()
                        val roleDisplayName = args[3]

                        val underscoreAsSpace: Boolean = when(argLength) {
                            4 -> {
                                false
                            }

                            5 -> {
                                if (!roleCommandAlias.matchOptionArgument(args[4], RoleCommandOption.RoleCommandOptionArgument.UnderscoreAsSpace)) {
                                    return@onMatchOption CommandSyntaxHandler.alertInvalidCommandArg(commander, args, argLength - 1)
                                }

                                true
                            }

                            else -> return@onMatchOption CommandSyntaxHandler.alertInvalidCommandArg(commander, args, argLength - 1)
                        }

                        roleManager.createRole(commander, roleCode, roleDisplayName, underscoreAsSpace)
                    }

                    RoleCommandOption.Remove -> {
                        if (!(argLength == 3 || argLength == 4)) {
                            return@onMatchOption CommandSyntaxHandler.alertInvalidCommandArg(commander, args, argLength - 1)
                        }

                        val roleCode = args[2].uppercase()

                        when(argLength) {
                            3 -> return@onMatchOption CommandSyntaxHandler.sendCommandSyntax(commander, "${ChatColor.RED}Are you sure to remove the role by role code " +
                                    "'${ChatColor.WHITE}${roleCode}${ChatColor.RED}'? Upon removal, player with this role will be unassigned. To continue, please add '${ChatColor.WHITE}confirm" +
                                    "${ChatColor.RED}' at the end of the command.")

                            4 -> {
                                if (!args[argLength - 1].equals("confirm", true)) {
                                    return@onMatchOption CommandSyntaxHandler.alertInvalidCommandArg(commander, args, argLength - 1)
                                }

                                roleManager.removeRole(commander, roleCode)
                            }

                            else -> return@onMatchOption CommandSyntaxHandler.alertInvalidCommandArg(commander, args, argLength - 1)
                        }
                    }

                    RoleCommandOption.Assign -> {
                        val argIndex = argLength - 1

                        if (argLength != 4) {
                            return@onMatchOption CommandSyntaxHandler.alertInvalidCommandArg(commander, args, argIndex)
                        }

                        val playerName = args[2]
                        val roleCode = args[3]

                        playerSessionManager.assignPlayerRole(commander, playerName, roleCode)
                    }

                    RoleCommandOption.Unassign -> {
                        val argIndex = argLength - 1

                        if (!(argLength == 3 || argLength == 4)) {
                            return@onMatchOption CommandSyntaxHandler.alertInvalidCommandArg(commander, args, argIndex)
                        }

                        val playerName = args[2]

                        when(argLength) {
                            3 -> return@onMatchOption CommandSyntaxHandler.sendCommandSyntax(commander, "${ChatColor.RED}Are you sure to unassign role from the player " +
                                    "'${ChatColor.WHITE}${playerName}${ChatColor.RED}'? Upon unassignment, player will not able to access certain feature based on permission that have " +
                                    "been granted before. To continue, please add '${ChatColor.WHITE}confirm${ChatColor.RED}' at the end of the command.")

                            4 -> {
                                if (!args[argLength - 1].equals("confirm", true)) {
                                    return@onMatchOption CommandSyntaxHandler.alertInvalidCommandArg(commander, args, argIndex)
                                }

                                playerSessionManager.unassignPlayerRole(commander, playerName)
                            }

                            else -> return@onMatchOption CommandSyntaxHandler.alertInvalidCommandArg(commander, args, argIndex)
                        }
                    }

                    else -> CommandSyntaxHandler.alertInvalidCommandArg(commander, args, argLength - 1)
                }
            }
        }

        override fun getTabCompletion(
            commander: CommandSender,
            accessibleCommand: ArrayList<AccessibleCommand>,
            args: Array<out String>
        ): MutableList<String> {
            return when(val argLength = args.size) {
                2 -> {
                    val argIndex = argLength - 1

                    CommandAlias.getAccessibleCommandOptions(
                        commander,
                        accessibleCommand,
                        roleCommandAlias,
                        CommandFilter(argIndex, args[argIndex])
                    )
                }

                3 -> {
                    val argIndex = argLength - 1

                    return roleCommandAlias.onMatchOption(args[1]) {
                        when(it) {
                            RoleCommandOption.Remove -> {
                                roleManager.getAllRole()
                                    .map { it.roleCode!! }
                                    .filter { it.startsWith(args[argIndex], true) }
                                    .toMutableList()
                            }

                            RoleCommandOption.Assign -> {
                                playerSessionManager.getAllPlayerData()
                                    .map { it.value }
                                    .filter { it.startsWith(args[argIndex], true) }
                                    .toMutableList()
                            }

                            RoleCommandOption.Unassign -> {
                                playerSessionManager.getAllPlayerData()
                                    .map { it.value }
                                    .filter { it.startsWith(args[argIndex], true) }
                                    .toMutableList()
                            }

                            else -> super.getTabCompletion(commander, accessibleCommand, args)
                        }
                    }
                }

                4 -> {
                    val argIndex = argLength - 1

                    return roleCommandAlias.onMatchOption(args[1]) {
                        when(it) {
                            RoleCommandOption.Assign -> {
                                roleManager.getAllRole()
                                    .map { it.roleCode!! }
                                    .filter { it.startsWith(args[argIndex], true) }
                                    .toMutableList()
                            }

                            else -> super.getTabCompletion(commander, accessibleCommand, args)
                        }
                    }
                }

                5 -> {
                    val argIndex = argLength - 1

                    return roleCommandAlias.onMatchOption(args[1]) {
                        when(it) {
                            RoleCommandOption.Create -> {
                                CommandAlias.getAccessibleCommandOptionArgs(
                                    commander,
                                    accessibleCommand,
                                    it,
                                    CommandFilter(argIndex, args[argIndex])
                                )
                            }

                            else -> super.getTabCompletion(commander, accessibleCommand, args)
                        }
                    }
                }

                else -> super.getTabCompletion(commander, accessibleCommand, args)
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