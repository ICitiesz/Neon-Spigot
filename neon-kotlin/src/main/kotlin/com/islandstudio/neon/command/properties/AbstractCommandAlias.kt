package com.islandstudio.neon.command.properties

import com.islandstudio.neon.player.security.permission.Permission
import kotlin.reflect.KClass

abstract class AbstractCommandAlias<T: AbstractCommandOption<*>> {
    abstract val alias: String
    abstract val requiredPermissions: ArrayList<Permission>
    open val commandOptions: ArrayList<T> = ArrayList()

    fun <U> onMatchOption(argValue: String, block: (T?) -> U): U {
        return block(commandOptions.find {
            it.option.equals(argValue, true)
        })
    }

    fun matchOptionArgument(argValue: String, targetOptionArg: AbstractCommandOptionArgument): Boolean {
        return argValue.equals(targetOptionArg.optionArg, true)
    }

    protected fun getAllCommandOptions(clazz: KClass<T>): ArrayList<T> {
        return clazz.sealedSubclasses
            .map { it.objectInstance as T }
            .toCollection(ArrayList())
    }
}