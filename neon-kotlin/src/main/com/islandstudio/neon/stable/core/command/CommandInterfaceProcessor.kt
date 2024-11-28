package com.islandstudio.neon.stable.core.command

import com.islandstudio.neon.stable.core.command.properties.CommandArgument
import com.islandstudio.neon.stable.core.command.properties.CommandSyntax
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object CommandInterfaceProcessor {
    fun sendCommandSyntax(commander: CommandSender, commandSyntax: CommandSyntax, vararg formatArgs: Any) {
        sendCommandSyntax(commander, String.format(commandSyntax.syntaxMessage, *formatArgs))
    }

    fun sendCommandSyntax(commander: CommandSender, syntaxMessage: String, vararg formatArgs: Any) {
        sendCommandSyntax(commander, String.format(syntaxMessage, *formatArgs))
    }

    fun sendCommandSyntax(commander: CommandSender, commandSyntax: CommandSyntax) {
        sendCommandSyntax(commander, commandSyntax.syntaxMessage)
    }

    fun sendCommandSyntax(commander: CommandSender, syntaxMessage: String) {
        commander.sendMessage("${NCommand.COMMAND_SYNTAX_PREFIX}$syntaxMessage")
    }

    // Design 1: Invalid or missing argument: ...debug test <- at position 2
    fun notifyInvalidArgument(commander: CommandSender, args: Array<out String>, argIndex: Int = 0) {
        with(argIndex) {
            if (argIndex == 0) {
                if (args.size == 1) {
                    return@with this
                }

                return@with args.size - 1
            }

            return@with argIndex
        }.also {
            val arg = args[it]
            val errorMessage = run {
                if (it == 0) {
                    return@run String.format(
                        CommandSyntax.INVALID_ARGUMENT.syntaxMessage,
                        "${ChatColor.GOLD}/neon ${ChatColor.UNDERLINE}$arg", 2)
                }

                return@run String.format(
                    CommandSyntax.INVALID_ARGUMENT.syntaxMessage,
                    "${ChatColor.GOLD}...${args[it - 1]} ${ChatColor.UNDERLINE}$arg", it + 2)
            }

            sendCommandSyntax(commander, errorMessage)
        }
    }

    fun notifyInvalidCommand(commander: CommandSender, command: String) {
        sendCommandSyntax(commander, CommandSyntax.INVALID_COMMAND, command)
    }

    fun hasCommandArgument(arg: String, refArg: CommandArgument, ignorecase: Boolean = true): Boolean {
        return refArg.argName.equals(arg, ignorecase)
    }

    fun hasConfirmation(arg: String):Boolean {
        return hasCommandArgument(arg, CommandArgument.CONFIRM, false)
    }
}