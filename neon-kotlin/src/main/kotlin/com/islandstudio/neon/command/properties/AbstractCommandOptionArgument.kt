package com.islandstudio.neon.command.properties

import com.islandstudio.neon.stable.player.security.permission.Permission

abstract class AbstractCommandOptionArgument(internal val commandOption: AbstractCommandOption<*>) {
    abstract val optionArg: String
    abstract val optionArgIndex: Int
    abstract val inheritPermission: Boolean
    open val requiredPermissions: ArrayList<Permission> = if (inheritPermission) {
        commandOption.requiredPermissions
    } else arrayListOf()
}