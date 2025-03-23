package com.islandstudio.neon.command.properties

import com.islandstudio.neon.command.CommandAlias
import com.islandstudio.neon.player.security.permission.Permission
import kotlin.reflect.KClass

abstract class AbstractCommandOption<T: AbstractCommandOptionArgument>(val commandAlias: CommandAlias<*>) {
    abstract val option: String
    abstract val optionIndex: Int
    abstract val inheritPermission: Boolean
    open val optionArguments: ArrayList<T> = arrayListOf()
    open val requiredPermissions: ArrayList<Permission> = if (inheritPermission) {
        commandAlias.requiredPermissions
    } else arrayListOf()


    protected fun getAllCommandOptionArguments(clazz: KClass<T>): ArrayList<T> {
        return clazz.sealedSubclasses
            .map { it.objectInstance as T }
            .filter { this == it.commandOption  }
            .toCollection(ArrayList())
    }
}