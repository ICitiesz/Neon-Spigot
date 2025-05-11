package com.islandstudio.neon.command.properties

import com.islandstudio.neon.command.processing.CommandSyntax
import com.islandstudio.neon.command.processing.CommandSyntaxHandler
import com.islandstudio.neon.player.security.permission.Permission
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.reflect.KClass

abstract class AbstractCommandAlias<T: AbstractCommandOption<*>> {
    abstract val alias: String
    abstract val requiredPermissions: ArrayList<Permission>
    open val commandOptions: ArrayList<T> = ArrayList()

    inline fun <U> onMatchOption(
        commander: CommandSender,
        argValue: String,
        accessibleCommand: AccessibleCommand?,
        onMatchOptionFunc: (commandOption: T?) -> U
    ): U {
        val matchOptionResult = commandOptions.find {
            it.option.equals(argValue, true)
        } ?: return onMatchOptionFunc(null)

        /* Check if the commander is player
        * If it is player, accessible command check is required
        *  */
        if (commander is Player) {
            accessibleCommand?.let {
                if (matchOptionResult.requiredPermissions.isEmpty()) return@let

                if (it.hasAccessibleCommandOption(matchOptionResult.option)) return@let

                return CommandSyntaxHandler.sendCommandSyntax(commander, CommandSyntax.INVALID_PERMISSION) as U
            }
        }

        return onMatchOptionFunc(matchOptionResult)
    }

    protected fun getAllCommandOptions(clazz: KClass<T>): ArrayList<T> {
        return clazz.sealedSubclasses
            .map { it.objectInstance as T }
            .toCollection(ArrayList())
    }
}