package com.islandstudio.neon.player.role

import com.islandstudio.neon.command.CommandAlias
import com.islandstudio.neon.command.ICommandDispatcherNew
import com.islandstudio.neon.command.option.RoleCommandOption
import com.islandstudio.neon.command.processing.CommandSyntaxHandler
import com.islandstudio.neon.command.properties.AccessibleCommand
import com.islandstudio.neon.player.session.PlayerSessionManagerNew
import com.islandstudio.neon.shared.core.di.IComponentProvider
import com.islandstudio.neon.shared.core.di.injectComponent
import com.islandstudio.neon.shared.experimental.utils.coroutines.CloseableCoroutineScope
import com.islandstudio.neon.util.NeonColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.asCompletableFuture
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

class RoleCommandDispatcher(private val roleManager: RoleManagerNew): ICommandDispatcherNew, IComponentProvider {
    private val roleCommandAlias = CommandAlias.RoleAlias
    private val playerSessionManager by injectComponent<PlayerSessionManagerNew>()

    override fun processCommand(commander: CommandSender, playerAccessibleCommand: AccessibleCommand?, args: Array<out String>) {
        val argLength = args.size.apply {
            if (this != 1) return@apply

            //TODO: GUI implementation
            return
        }

        roleCommandAlias.onMatchOption(commander, args[1], playerAccessibleCommand) { commandOption ->
            when (commandOption) {
                RoleCommandOption.Create -> {
                    val maxInputLength = 64

                    if (!CommandAlias.validateCommandOptionArgLength(argLength, 4, 5)) {
                        return CommandSyntaxHandler.alertInvalidCommandArg(commander, args)
                    }

                    val roleCode = args[2].uppercase().also {
                        if (it.length > maxInputLength) {
                            return CommandSyntaxHandler.sendCommandSyntax(commander, "${NeonColor.DefinedColor.Red}Role code should not exceed 64 characters!")
                        }
                    }
                    val roleDisplayName = args[3].also {
                        if (it.length > maxInputLength) {
                            return CommandSyntaxHandler.sendCommandSyntax(commander, "${NeonColor.DefinedColor.Red}Role display name should not exceed 64 characters!")
                        }
                    }

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

                    CloseableCoroutineScope(Dispatchers.IO).launchJob {
                        roleManager.createRole(commander, roleCode, roleDisplayName, underscoreAsSpace)
                    }
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

                            CloseableCoroutineScope(Dispatchers.IO).launchJob {
                                roleManager.removeRole(commander, roleCode)
                            }
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

                    CloseableCoroutineScope(Dispatchers.IO).launchJob {
                        roleManager.assignPlayerRole(commander, playerName, roleCode)
                    }
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

                            CloseableCoroutineScope(Dispatchers.IO).launchJob {
                                roleManager.unassignPlayerRole(commander, playerName)
                            }
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

                                CloseableCoroutineScope(Dispatchers.IO).launchJob {
                                    roleManager.updateRoleCode(commander, oldRoleCode, newRoleCode)
                                }
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

                                CloseableCoroutineScope(Dispatchers.IO).launchJob {
                                    roleManager.updateRoleDisplayName(commander, roleCode, newRoleDisplayName, underscoreAsSpace)
                                }
                            }

                            else -> return CommandSyntaxHandler.alertInvalidCommandArg(commander, args)
                        }
                    }
                }

                else -> CommandSyntaxHandler.alertInvalidCommandArg(commander, args)
            }
        }
    }

    override fun getTabCompletion(commander: CommandSender, playerAccessibleCommand: AccessibleCommand?, args: Array<out String>): MutableList<String> {
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
                            CloseableCoroutineScope(Dispatchers.IO).launchAsCompletableDeferredResult {
                                CommandAlias.getTabCompleteSuggestion(roleManager.getAllRole(), args[argIndex]) {
                                    this.filter { x -> x.code.isNotEmpty() }.map { x -> x.code }
                                }
                            }.asCompletableFuture().get()
                        }

                        RoleCommandOption.Assign -> {
                            CommandAlias.getTabCompleteSuggestion(
                                playerSessionManager.getAllPlayers().map { it.name }, args[argIndex]
                            )
                        }

                        RoleCommandOption.Unassign -> {
                            CommandAlias.getTabCompleteSuggestion(
                                playerSessionManager.getAllPlayers().map { it.name }, args[argIndex]
                            )
                        }

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
                        RoleCommandOption.Assign -> {
                            return CloseableCoroutineScope(Dispatchers.IO).launchAsCompletableDeferredResult {
                                CommandAlias.getTabCompleteSuggestion(roleManager.getAllRole(), args[argIndex]) {
                                    this.filter { x -> x.code.isNotEmpty() }.map { x -> x.code }
                                }
                            }.asCompletableFuture().get()
                        }

                        else -> return@onMatchOption
                    }
                }

                roleCommandAlias.onMatchOption(commander, args[2], playerAccessibleCommand) { commandOption ->
                    when(commandOption) {
                        RoleCommandOption.RoleCode -> {
                            CloseableCoroutineScope(Dispatchers.IO).launchAsCompletableDeferredResult {
                                CommandAlias.getTabCompleteSuggestion(roleManager.getAllRole(), args[argIndex]) {
                                    this.filter { x -> x.code.isNotEmpty() }.map { x -> x.code }
                                }
                            }.asCompletableFuture().join()
                        }

                        RoleCommandOption.RoleDisplayName -> {
                            CloseableCoroutineScope(Dispatchers.IO).launchAsCompletableDeferredResult {
                                CommandAlias.getTabCompleteSuggestion(roleManager.getAllRole(), args[argIndex]) {
                                    this.filter { x -> x.code.isNotEmpty() }.map { x -> x.code }
                                }
                            }.asCompletableFuture().join()
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