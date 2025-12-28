package com.islandstudio.neon.player.permission

import com.islandstudio.neon.command.CommandAlias
import com.islandstudio.neon.command.ICommandDispatcherNew
import com.islandstudio.neon.command.option.PermissionCommandOption
import com.islandstudio.neon.command.processing.CommandSyntaxHandler
import com.islandstudio.neon.command.properties.AccessibleCommand
import com.islandstudio.neon.player.role.RoleManagerNew
import com.islandstudio.neon.shared.core.di.IComponentProvider
import com.islandstudio.neon.shared.core.di.injectComponent
import com.islandstudio.neon.shared.experimental.utils.coroutines.CloseableCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.asCompletableFuture
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

class PermissionCommandDispatcher(private val permissionManager: PermissionManager): ICommandDispatcherNew, IComponentProvider {
    private val roleManager by injectComponent<RoleManagerNew>()
    private val permissionCommandAlias = CommandAlias.PermissionAlias
    private val closeableCoroutineScope = CloseableCoroutineScope(Dispatchers.IO)

    override fun processCommand(commander: CommandSender, playerAccessibleCommand: AccessibleCommand?, args: Array<out String>) {
        val argLength = args.size.apply {
            if (this != 1) return@apply

            // TODO: GUI implementation

            return
        }

        permissionCommandAlias.onMatchOption(commander, args[1], playerAccessibleCommand) { commandOption ->
            when(commandOption) {
                PermissionCommandOption.Grant -> {
                    if (argLength < 4) {
                        return@onMatchOption CommandSyntaxHandler.alertInvalidCommandArg(commander, args)
                    }

                    val roleCode = args[2].uppercase()
                    val permissionCodes = args
                        .toMutableList() // Split and group permission codes
                        .subList(3, argLength)
                        .distinctBy { x -> x.uppercase() }
                        .toCollection(ArrayList())

                    closeableCoroutineScope.launchJob {
                        permissionManager.grantPermission(commander, roleCode, permissionCodes)
                    }
                }

                PermissionCommandOption.Revoke -> {
                    if (argLength < 4) {
                        return@onMatchOption CommandSyntaxHandler.alertInvalidCommandArg(commander, args)
                    }

                    val roleCode = args[2].uppercase()
                    val permissionCodes = args.toMutableList().subList(3, argLength).run {
                        val tempList = this.filter { x -> !CommandAlias.validateConfirmation(x) }.also {
                            if (it.isNotEmpty()) return@also

                            return@onMatchOption CommandSyntaxHandler.sendCommandSyntax(commander, "${ChatColor.RED}Please provide permission(s) to revoke!")
                        }

                        if (CommandAlias.validateConfirmation(this.last())) {
                            return@run tempList
                                .distinctBy { x -> x.uppercase() }
                                .toCollection(ArrayList())
                        }

                        return@onMatchOption CommandSyntaxHandler.sendCommandSyntax(commander, "${ChatColor.RED}Are you sure to revoke these permission(s) from the role " +
                                "'${ChatColor.WHITE}${roleCode}${ChatColor.RED}'? Upon revocation, player will not able to access certain feature. " +
                                "To continue, please add '${ChatColor.WHITE}confirm${ChatColor.RED}' at the end of the command.")
                    }

                    closeableCoroutineScope.launchJob {
                        permissionManager.revokePermission(commander, roleCode, permissionCodes)
                    }
                }

                PermissionCommandOption.RevokeAll -> {
                    if (!CommandAlias.validateCommandOptionArgLength(argLength, 3, 4)) {
                        return@onMatchOption CommandSyntaxHandler.alertInvalidCommandArg(commander, args)
                    }

                    val roleCode = args[2].uppercase()

                    if (!CommandAlias.validateConfirmation(args.last())) {
                        return@onMatchOption CommandSyntaxHandler.sendCommandSyntax(commander, "${ChatColor.RED}Are you sure to revoke all permissions from the role " +
                                "'${ChatColor.WHITE}${roleCode}${ChatColor.RED}'? Upon revocation, player will not able to access certain feature. " +
                                "To continue, please add '${ChatColor.WHITE}confirm${ChatColor.RED}' at the end of the command.")
                    }

                    // TODO: Need to refine revoke all permission
                    //accessControlManager.revokePermission(commander, roleCode, arrayListOf(), true)
                    closeableCoroutineScope.launchJob {
                        permissionManager.revokePermission(commander, roleCode, arrayListOf())
                    }
                }

                else -> CommandSyntaxHandler.alertInvalidCommandArg(commander, args)
            }
        }
    }

    override fun getTabCompletion(commander: CommandSender, playerAccessibleCommand: AccessibleCommand?, args: Array<out String>): MutableList<String> {
        val argLength = args.size

        return when {
            argLength == 2 -> {
                CommandAlias.getAccessibleCommandOptions(
                    commander,
                    permissionCommandAlias,
                    playerAccessibleCommand,
                    args[argLength - 1]
                )
            }

            argLength == 3 -> {
                permissionCommandAlias.onMatchOption(
                    commander,
                    args[1],
                    playerAccessibleCommand
                ) { permissionCommandOption ->
                    when (permissionCommandOption) {
                        PermissionCommandOption.Grant,
                        PermissionCommandOption.Revoke,
                        PermissionCommandOption.RevokeAll -> {
                            val tabCompleteSuggestion = mutableListOf<String>()

                            closeableCoroutineScope.launchAsCompletableDeferred {
                                tabCompleteSuggestion.addAll(
                                    CommandAlias.getTabCompleteSuggestion(roleManager.getAllRole(), args[argLength - 1]) {
                                        this.filter { x -> x.code.isNotEmpty() }.map { x -> x.code }
                                    }
                                )
                            }.asCompletableFuture().join()

                            tabCompleteSuggestion
                        }

                        else -> super.getTabCompletion(commander, playerAccessibleCommand, args)
                    }
                }
            }

            argLength >= 4 -> {
                permissionCommandAlias.onMatchOption(
                    commander,
                    args[1],
                    playerAccessibleCommand
                ) { permissionCommandOption ->
                    when (permissionCommandOption) {
                        PermissionCommandOption.Grant,
                        PermissionCommandOption.Revoke -> {
                            val tabCompleteSuggestion = mutableListOf<String>()

                            closeableCoroutineScope.launchAsCompletableDeferred {
                                tabCompleteSuggestion.addAll(
                                    CommandAlias.getTabCompleteSuggestion(permissionManager.getAllPermissionFromDatabase(), args[argLength - 1]) {
                                        this.filter { x -> x.code.isNotEmpty() }.map { x -> x.code }
                                    }
                                )
                            }.asCompletableFuture().join()

                            tabCompleteSuggestion
                        }

                        else -> super.getTabCompletion(commander, playerAccessibleCommand, args)
                    }

                }
            }

            else -> super.getTabCompletion(commander, playerAccessibleCommand, args)
        }
    }
}