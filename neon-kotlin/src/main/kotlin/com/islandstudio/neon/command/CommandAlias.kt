package com.islandstudio.neon.command

import com.islandstudio.neon.command.option.NWaypointsCommandOption
import com.islandstudio.neon.command.option.NeonFeatureCommandOption
import com.islandstudio.neon.command.option.PermissionCommandOption
import com.islandstudio.neon.command.option.RoleCommandOption
import com.islandstudio.neon.command.properties.AbstractCommandAlias
import com.islandstudio.neon.command.properties.AbstractCommandOption
import com.islandstudio.neon.command.properties.AccessibleCommand
import com.islandstudio.neon.player.permission.NeonPermission
import com.islandstudio.neon.player.permission.PermissionManager
import com.islandstudio.neon.player.session.PlayerSessionManagerNew
import com.islandstudio.neon.shared.core.di.IComponentProvider
import com.islandstudio.neon.shared.core.di.getComponent
import com.islandstudio.neon.shared.experimental.utils.coroutines.CloseableCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

sealed class CommandAlias<T: AbstractCommandOption<*>>: AbstractCommandAlias<T>() {
    companion object: IComponentProvider {
        fun getAllCommandAlias(): ArrayList<CommandAlias<*>> {
            return CommandAlias::class.sealedSubclasses
                .map {
                    it.objectInstance as CommandAlias
                }.toCollection(ArrayList())
        }

        fun getAccessibleCommands(player: Player): ArrayList<AccessibleCommand> {
            val accessibleCommand = ArrayList<AccessibleCommand>()

            val playerSessionManager = getComponent<PlayerSessionManagerNew>()
            val permissionManager = getComponent<PermissionManager>()

            CloseableCoroutineScope(Dispatchers.IO).launchJob {
                val grantedRolePermissions = async {
                    playerSessionManager.getPlayerSession(player)!!.roleId?.let {
                        permissionManager.getGrantedRolePermissions(it)
                    } ?: return@async arrayListOf()
                }.await()

                val grantedParentPermissionCodes = grantedRolePermissions
                    .filter { permission -> permission.parentId == null }
                    .map { permission -> permission.permissionCode }
                val grantedSubPermissionCodes = grantedRolePermissions
                    .filter { permission -> permission.parentId != null }
                    .map { permission -> permission.permissionCode }

                val processedCommandAlias = getAllCommandAlias()
                    .filter { commandAlias ->
                        val cmdPermissionCodes = commandAlias.requiredNeonPermissions.run {
                            /* If required permission is empty, means everyone can access the command */
                            if (this.isEmpty()) return@filter true

                            this.map { it.permissionCode }
                        }

                        grantedParentPermissionCodes.containsAll(cmdPermissionCodes)
                    }.map { commandAlias ->
                        val cmdPermissionCodes = commandAlias.requiredNeonPermissions.run {
                            if (this.isEmpty()) return@run arrayListOf()

                            this.map { it.permissionCode }
                        }

                        val accessibleCommandOptions = commandAlias.commandOptions.filter {
                            val commandOptionPermissionCodes = it.requiredNeonPermissions.map { x -> x.permissionCode }

                            /* If required permission is empty, means everyone can access the command */
                            if (cmdPermissionCodes.isEmpty() || commandOptionPermissionCodes.isEmpty()) {
                                return@filter true
                            }

                            if (it.inheritPermission) {
                                return@filter grantedParentPermissionCodes.any { x -> x in commandOptionPermissionCodes }
                            }

                            grantedSubPermissionCodes.any { x -> x in commandOptionPermissionCodes }
                        }.map {
                            val commandOptionPermissionCodes = it.requiredNeonPermissions.map { x -> x.permissionCode }
                            val commandOptionArgs = it.optionArguments

                            if (cmdPermissionCodes.isEmpty() || commandOptionPermissionCodes.isEmpty()) {
                                return@map AccessibleCommand.AccessibleCommandOption(
                                    it.option,
                                    commandOptionArgs
                                        .map { commandOptionArg -> commandOptionArg.optionArg }
                                        .toCollection(ArrayList())
                                )
                            }

                            AccessibleCommand.AccessibleCommandOption(
                                it.option,
                                commandOptionArgs
                                    .filter { x ->
                                        val commandOptionArgPermissionCodes = x.requiredNeonPermissions
                                            .map { requiredPermission -> requiredPermission.permissionCode }
                                            .toMutableList()

                                        if (commandOptionArgPermissionCodes.isEmpty()) return@filter true

                                        grantedParentPermissionCodes.any { grantedSubPermissionCode -> grantedSubPermissionCode in commandOptionArgPermissionCodes }
                                    }
                                    .map { x -> x.optionArg }
                                    .toCollection(ArrayList())
                            )
                        }.toCollection(ArrayList())

                        AccessibleCommand(commandAlias.alias, accessibleCommandOptions)
                    }

                accessibleCommand.addAll(processedCommandAlias)
            }

            return accessibleCommand
        }

        fun getAccessibleCommandOptions(commander: CommandSender, commandAlias: CommandAlias<*>, playerAccessibleCommand: AccessibleCommand?, argValue: String, commandOptionIndex: Int = 1): MutableList<String> {
           return if (commander is Player) {
               playerAccessibleCommand?.let {
                   val accessibleCommandOption = it.accessibleCommandOptions.map { x -> x.commandOption }

                   commandAlias.commandOptions
                       .asSequence()
                       .filter { x -> x.optionIndex == commandOptionIndex }
                       .filter { x -> x.option in accessibleCommandOption }
                       .filter { x -> x.option.startsWith(argValue, true) }
                       .map { x -> x.option }
                       .toMutableList()
               } ?: mutableListOf()
           } else {
               commandAlias.commandOptions
                   .filter { x -> x.optionIndex == commandOptionIndex }
                   .filter { x -> x.option.startsWith(argValue, true) }
                   .map { x -> x.option }
                   .toMutableList()
           }
        }

        fun getAccessibleCommandOptionArgs(commander: CommandSender, argValue: String, commandOption: AbstractCommandOption<*>, playerAccessibleCommand: AccessibleCommand?): MutableList<String> {
            return if (commander is Player) {
                playerAccessibleCommand?.let {
                    if (!it.command.equals(commandOption.commandAlias.alias, true)) return mutableListOf()

                    val accessibleCommandOption = it.accessibleCommandOptions.find { x ->
                        x.commandOption.equals(commandOption.option, true)
                    } ?: return mutableListOf()

                    commandOption.optionArguments
                        .filter { x -> x.optionArg in accessibleCommandOption.accessibleCommandOptionArgs }
                        .filter { x -> x.optionArg.startsWith(argValue, true) }
                        .map { x -> x.optionArg }
                        .toMutableList()
                } ?: mutableListOf()
            } else {
                commandOption.optionArguments
                    .filter { x -> x.optionArg.startsWith(argValue, true) }
                    .map { x -> x.optionArg}
                    .toMutableList()
            }
        }

        fun <T> getTabCompleteSuggestion(
            completionValue: Collection<T>,
            refValue: String,
            ignoreCase: Boolean = true,
            block: Collection<T>.() -> Collection<String> = { completionValue.map { it.toString() } }
        ): MutableList<String> {
            return block(completionValue)
                .filter { it.startsWith(refValue, ignoreCase) }
                .toMutableList()
        }

        fun validateCommandOptionArgLength(currentArgLength: Int, vararg argLengths: Int): Boolean {
            return currentArgLength in argLengths
        }

        fun validateConfirmation(argValue: String): Boolean {
            return argValue.equals("confirm", true)
        }
    }

    data object RoleAlias: CommandAlias<RoleCommandOption>() {
        override val alias: String = "role"
        override val requiredNeonPermissions: ArrayList<NeonPermission> = arrayListOf(
            NeonPermission.RoleManagement
        )
        override val commandOptions: ArrayList<RoleCommandOption> = getAllCommandOptions(RoleCommandOption::class)
    }

    data object PermissionAlias: CommandAlias<PermissionCommandOption>() {
        override val alias: String = "permission"
        override val requiredNeonPermissions: ArrayList<NeonPermission> = arrayListOf(
            NeonPermission.NeonPermissionManagement
        )
        override val commandOptions: ArrayList<PermissionCommandOption> = getAllCommandOptions(PermissionCommandOption::class)
    }

    data object NWaypointsAlias: CommandAlias<NWaypointsCommandOption>() {
        override val alias: String = "waypoints"
        override val requiredNeonPermissions: ArrayList<NeonPermission> = arrayListOf()
        override val commandOptions: ArrayList<NWaypointsCommandOption> = getAllCommandOptions(NWaypointsCommandOption::class)
    }

    data object NeonFeatureAlias: CommandAlias<NeonFeatureCommandOption>() {
        override val alias: String = "feature"
        override val requiredNeonPermissions: ArrayList<NeonPermission> = arrayListOf(
            NeonPermission.NeonFeatureManagement
        )
        override val commandOptions: ArrayList<NeonFeatureCommandOption> = getAllCommandOptions(
            NeonFeatureCommandOption::class)
    }
}