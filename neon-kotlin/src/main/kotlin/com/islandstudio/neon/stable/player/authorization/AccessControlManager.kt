package com.islandstudio.neon.stable.player.authorization

import com.islandstudio.neon.shared.core.IRunner
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.stable.command.Command
import com.islandstudio.neon.stable.command.ICommandDispatcher
import org.bukkit.command.CommandSender
import org.koin.core.annotation.Single

@Single
class AccessControlManager: IComponentInjector {
    /*
    * RolePermission -> Store permission for role
    * Permission -> Store as reference
    *
    * Database Table:
    * T_ROLE_PERMISSION
    * T_PERMISSION
    *  */

    companion object: IRunner, ICommandDispatcher {
        private val permissionCommand = Command.PermissionCommand

        override fun run() {
            TODO("Not yet implemented")
        }

        override fun getCommandDispatcher(
            commander: CommandSender,
            args: Array<out String>
        ) {
            TODO("Not yet implemented")
        }

        override fun getTabCompletion(
            commander: CommandSender,
            args: Array<out String>
        ): MutableList<String> {
            return super.getTabCompletion(commander, args)
        }
    }

    fun grantPermission() {

    }

    fun revokePermission() {

    }
}