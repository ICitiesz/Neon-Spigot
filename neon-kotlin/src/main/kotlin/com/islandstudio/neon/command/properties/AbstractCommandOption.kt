package com.islandstudio.neon.command.properties

import com.islandstudio.neon.command.CommandAlias
import com.islandstudio.neon.player.permission.NeonPermission
import kotlin.reflect.KClass

abstract class AbstractCommandOption<T: AbstractCommandOptionArgument>(val commandAlias: CommandAlias<*>) {
    abstract val option: String
    abstract val optionIndex: Int
    abstract val inheritPermission: Boolean
    open val optionArguments: ArrayList<T> = arrayListOf()
    open val requiredNeonPermissions: ArrayList<NeonPermission>
        get() = if (inheritPermission) commandAlias.requiredNeonPermissions else arrayListOf()

    protected fun getAllCommandOptionArguments(clazz: KClass<T>): ArrayList<T> {
        return clazz.sealedSubclasses
            .map { it.objectInstance as T }
            .filter { this == it.commandOption  }
            .toCollection(ArrayList())
    }
}