package com.islandstudio.neon.command.properties

import com.islandstudio.neon.command.processing.CommandSyntaxHandler
import com.islandstudio.neon.player.permission.NeonPermission
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

abstract class AbstractCommandOptionArgument(internal val commandOption: AbstractCommandOption<*>) {
    abstract val optionArg: String
    abstract val optionArgIndex: Int
    abstract val inheritPermission: Boolean
    open val requiredNeonPermissions: ArrayList<NeonPermission>
        get() = if (inheritPermission) { commandOption.requiredNeonPermissions } else arrayListOf()

    fun matchOptionArgument(
        commander: CommandSender,
        accessibleCommand: AccessibleCommand?,
        args: Array<out String>,
    ): Boolean {
        val argValue = args[optionArgIndex]
        val matchOptionArgResult = optionArg.equals(argValue, true)

        run {
            if (!matchOptionArgResult) return@run

            if (commander !is Player) return@run

            if (requiredNeonPermissions.isEmpty()) return@run

            accessibleCommand?.let {
                if (it.hasAccessibleCommandOptionArg(commandOption.option, optionArg)) return@run
            }

            CommandSyntaxHandler.alertInvalidCommandArg(commander, args, this)
            return false
        }

        return matchOptionArgResult.also {
            if (!it) CommandSyntaxHandler.alertInvalidCommandArg(commander, args, this)
        }
    }
}