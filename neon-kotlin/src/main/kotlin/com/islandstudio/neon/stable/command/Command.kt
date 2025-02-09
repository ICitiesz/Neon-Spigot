package com.islandstudio.neon.stable.command

import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.stable.player.authorization.PermissionEnum
import com.islandstudio.neon.stable.player.authorization.RoleManager
import com.islandstudio.neon.stable.player.session.PlayerSessionManager
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.koin.core.component.inject

sealed class Command: AbstractCommand() {
    companion object: IComponentInjector {
        fun getAccessibleCommands(commander: CommandSender): ArrayList<AccessibleCommand> {
            if (commander is Player) {
                val playerSessionManager by inject<PlayerSessionManager>()
                val roleManager by inject<RoleManager>()
                val playerRole = RoleManager


                playerSessionManager.getPlayerSession(commander)
            }


            return arrayListOf()
        }

        fun isConsoleCommandSender(commander: CommandSender): Boolean {
            return commander is ConsoleCommandSender
        }

        fun isCommandAccessible(commander: CommandSender): Boolean {
            if (isConsoleCommandSender(commander)) return true

            // TODO: Need validate permission
            return true
        }
    }

    data object RoleCommand: Command() {
        override val alias: String = "role"
        override val commandArguments: ArrayList<CommandArgument> = arrayListOf(
            CommandArgument(
                "create",
                1,
                true
            ),
            CommandArgument(
                "remove",
                1,
                true
            ),
            CommandArgument(
                "assign",
                1,
                true
            ),
            CommandArgument(
                "unassign",
                1,
                true
            )
        )

        override val requiredPermissions: ArrayList<PermissionEnum> = arrayListOf(
            PermissionEnum.ROLE_MANAGEMENT
        )
    }

    data object PermissionCommand: Command() {
        override val alias: String
            get() = TODO("Not yet implemented")
        override val commandArguments: ArrayList<CommandArgument>
            get() = TODO("Not yet implemented")
        override val requiredPermissions: ArrayList<PermissionEnum>
            get() = TODO("Not yet implemented")

    }
}