package com.islandstudio.neon.command

import com.islandstudio.neon.command.option.NWaypointsCommandOption
import com.islandstudio.neon.command.option.PermissionCommandOption
import com.islandstudio.neon.command.option.RoleCommandOption
import com.islandstudio.neon.command.option.ServerFeaturesCommandOption
import com.islandstudio.neon.command.properties.AbstractCommandAlias
import com.islandstudio.neon.command.properties.AbstractCommandOption
import com.islandstudio.neon.command.properties.AccessibleCommand
import com.islandstudio.neon.command.properties.CommandFilter
import com.islandstudio.neon.player.security.AccessControlManager
import com.islandstudio.neon.player.security.permission.Permission
import com.islandstudio.neon.player.session.PlayerSessionManager
import com.islandstudio.neon.shared.core.di.IComponentInjector
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.koin.core.component.inject

sealed class CommandAlias<T: AbstractCommandOption<*>>: AbstractCommandAlias<T>() {
    companion object: IComponentInjector {
        fun getAllCommandAlias(): ArrayList<CommandAlias<*>> {
            return CommandAlias::class.sealedSubclasses
                .map {
                    it.objectInstance as CommandAlias
                }.toCollection(ArrayList())
        }

        fun getAccessibleCommands(commander: CommandSender): ArrayList<AccessibleCommand> {
            val accessibleCommand = ArrayList<AccessibleCommand>()

            return when(commander) {
                is Player -> {
                    val playerSessionManager by inject<PlayerSessionManager>()
                    val accessControlManager by inject<AccessControlManager>()

                    val grantedRolePermissions = playerSessionManager.getPlayerSession(commander)!!.roleId?.let {
                        accessControlManager.getGrantedRolePermission(it)
                    } ?: return arrayListOf()

                    val grantedPermissionCodes = accessControlManager.getGrantedParentPermission(grantedRolePermissions)
                        .map { permission -> permission.permissionCode }

                    val grantedSubPermissionCodes = accessControlManager.getGrantedChildPermission(grantedRolePermissions)
                        .map { permission -> permission.permissionCode }

                    val processedCommandAlias = getAllCommandAlias()
                        .filter { commandAlias ->
                            val cmdPermissionCodes = commandAlias.requiredPermissions.run {
                                /* If required permission is empty, means everyone can access the command */
                                if (this.isEmpty()) return@filter true

                                this.map { it.permissionCode }
                            }

                            grantedPermissionCodes.containsAll(cmdPermissionCodes)
                        }.map { commandAlias ->
                            val cmdPermissionCodes = commandAlias.requiredPermissions.run {
                                if (this.isEmpty()) return@run arrayListOf()

                                this.map { it.permissionCode }
                            }

                            val accessibleCommandOptions = commandAlias.commandOptions.filter {
                                val commandOptionPermissionCodes = it.requiredPermissions.map { x -> x.permissionCode }

                                /* If required permission is empty, means everyone can access the command */
                                if (cmdPermissionCodes.isEmpty() || commandOptionPermissionCodes.isEmpty()) {
                                    return@filter true
                                }

                                if (it.inheritPermission) {
                                    return@filter grantedPermissionCodes.any { x -> x in commandOptionPermissionCodes }
                                }

                                grantedSubPermissionCodes.any { x -> x in commandOptionPermissionCodes }
                            }.map {
                                val commandOptionPermissionCodes = it.requiredPermissions.map { x -> x.permissionCode }
                                val commandOptionArgs = it.optionArguments

                                if (cmdPermissionCodes.isEmpty() || commandOptionPermissionCodes.isEmpty()) {
                                    return@map AccessibleCommand.AccessibleCommandOption(
                                        it.option,
                                        commandOptionArgs.map { it.optionArg }.toCollection(ArrayList())
                                    )
                                }

                                AccessibleCommand.AccessibleCommandOption(
                                    it.option,
                                    commandOptionArgs
                                        .filter { x ->
                                            val commandOptionArgPermissionCodes = x.requiredPermissions
                                                .map { it.permissionCode }
                                                .toMutableList()

                                            if (commandOptionArgPermissionCodes.isEmpty()) {
                                                return@filter true
                                            }

                                            grantedSubPermissionCodes.any { it in commandOptionPermissionCodes }
                                        }
                                        .map { x -> x.optionArg }
                                        .toCollection(ArrayList())
                                )
                            }.toCollection(ArrayList())

                            AccessibleCommand(commandAlias.alias, accessibleCommandOptions)
                        }

                    accessibleCommand.apply {
                        this.addAll(processedCommandAlias)
                    }
                }

                is ConsoleCommandSender -> {
                    accessibleCommand.addAll(
                        getAllCommandAlias()
                            .map {
                                val commandOptions = it.commandOptions
                                    .map {
                                        val commandOptionArgs = it.optionArguments.map { it.optionArg }.toCollection(ArrayList())

                                        AccessibleCommand.AccessibleCommandOption(it.option, commandOptionArgs)
                                    }
                                    .toCollection(ArrayList())

                                AccessibleCommand(it.alias, commandOptions)
                            }
                    )

                    accessibleCommand
                }

                else -> accessibleCommand
            }
        }

        fun getAccessibleCommandOptions(
            commander: CommandSender,
            accessibleCommands: ArrayList<AccessibleCommand>,
            targetCommand: CommandAlias<*>,
            commandFilter: CommandFilter? = null
        ): ArrayList<String> {
            return when(commander) {
                is Player -> {
                    accessibleCommands.find { it.command.equals(targetCommand.alias, true) }
                        ?.let { accessibleCommand ->
                            targetCommand.commandOptions
                                .filter { it.option in accessibleCommand.accessibleCommandOptions.map { x -> x.commandOption } }
                                .filter {
                                    commandFilter?.let { argFilter ->
                                        return@filter it.optionIndex == argFilter.argIndex
                                    } ?: true
                                }
                                .map { it.option }
                                .filter {
                                    commandFilter?.let { argFilter ->
                                        return@filter it.startsWith(argFilter.filterRefArg, true)
                                    } ?: true
                                }
                                .toCollection(ArrayList())
                        } ?: arrayListOf()
                }

                is ConsoleCommandSender -> {
                    targetCommand.commandOptions
                        .filter {
                            commandFilter?.let { cmdFilter ->
                                return@filter it.optionIndex == cmdFilter.argIndex
                            } ?: true
                        }
                        .map { it.option }
                        .filter {
                            commandFilter?.let { cmdFilter ->
                                return@filter it.startsWith(cmdFilter.filterRefArg, true)
                            } ?: true
                        }
                        .toCollection(ArrayList())
                }

                else -> arrayListOf()
            }
        }

        fun getAccessibleCommandOptionArgs(
            commander: CommandSender,
            accessibleCommand: ArrayList<AccessibleCommand>,
            targetCommandOption: AbstractCommandOption<*>,
            commandFilter: CommandFilter? = null
        ): ArrayList<String> {
            return when(commander) {
                is Player -> {
                    accessibleCommand.find { it.command.equals(targetCommandOption.commandAlias.alias, true) }
                        ?.let {
                            it.accessibleCommandOptions.find { it.commandOption.equals(targetCommandOption.option, true) }
                            ?.let { accessibleCommandOption ->
                                targetCommandOption.optionArguments
                                    .filter { it.optionArg in accessibleCommandOption.accessibleCommandOptionArgs }
                                    .filter {
                                        commandFilter?.let { cmdFilter ->
                                            return@filter it.optionArgIndex == cmdFilter.argIndex
                                        } ?: true
                                    }.map { it.optionArg }
                                    .filter {
                                        commandFilter?.let { cmdFilter ->
                                            return@filter it.startsWith(cmdFilter.filterRefArg, true)
                                        } ?: true
                                    }.toCollection(ArrayList())
                            }
                        } ?: arrayListOf()
                }

                is ConsoleCommandSender -> {
                    targetCommandOption.optionArguments
                        .filter {
                            commandFilter?.let { cmdFilter ->
                                return@filter it.optionArgIndex == cmdFilter.argIndex
                            } ?: true
                        }
                        .map { it.optionArg }
                        .filter {
                            commandFilter?.let { cmdFilter ->
                                return@filter it.startsWith(cmdFilter.filterRefArg, true)
                            } ?: true
                        }
                        .toCollection(ArrayList())
                }

                else -> arrayListOf()
            }
        }

        fun checkCommandOptionAccess(commandOption: AbstractCommandOption<*>, accessibleCommandOptions: ArrayList<String>): Boolean {
            return commandOption.option in accessibleCommandOptions
        }
    }

    data object RoleAlias: CommandAlias<RoleCommandOption>() {
        override val alias: String = "role"
        override val requiredPermissions: ArrayList<Permission> = arrayListOf(
            Permission.RoleManagement
        )
        override val commandOptions: ArrayList<RoleCommandOption> = getAllCommandOptions(RoleCommandOption::class)
    }

    data object PermissionAlias: CommandAlias<PermissionCommandOption>() {
        override val alias: String = "permission"
        override val requiredPermissions: ArrayList<Permission> = arrayListOf(
            Permission.PermissionManagement
        )
        override val commandOptions: ArrayList<PermissionCommandOption> = getAllCommandOptions(PermissionCommandOption::class)
    }

    data object NWaypointsAlias: CommandAlias<NWaypointsCommandOption>() {
        override val alias: String = "waypoints"
        override val requiredPermissions: ArrayList<Permission> = arrayListOf()
        override val commandOptions: ArrayList<NWaypointsCommandOption> = getAllCommandOptions(NWaypointsCommandOption::class)
    }

    data object ServerFeaturesAlias: CommandAlias<ServerFeaturesCommandOption>() {
        override val alias: String = "serverfeatures"
        override val requiredPermissions: ArrayList<Permission> = arrayListOf(
            Permission.ServerFeaturesManagement
        )
        override val commandOptions: ArrayList<ServerFeaturesCommandOption> = getAllCommandOptions(
            ServerFeaturesCommandOption::class)
    }
}