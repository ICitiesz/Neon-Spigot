package com.islandstudio.neon.stable.command

import com.islandstudio.neon.stable.player.authorization.PermissionEnum

abstract class AbstractCommand {
    abstract val alias: String
    abstract val commandArguments: ArrayList<CommandArgument>
    abstract val requiredPermissions: ArrayList<PermissionEnum>

    data class CommandArgument(
        val arg: String,
        val argIndex: Int,
        val inheritParentPermissions: Boolean,
        val requiredPermissions: ArrayList<PermissionEnum> = arrayListOf()
    )
}