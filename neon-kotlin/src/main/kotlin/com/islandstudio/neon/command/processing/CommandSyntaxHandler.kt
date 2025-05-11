package com.islandstudio.neon.command.processing

import com.islandstudio.neon.command.properties.AbstractCommandOptionArgument
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object CommandSyntaxHandler {
    val COMMAND_SYNTAX_PREFIX = "${ChatColor.WHITE}[${ChatColor.AQUA}Neon${ChatColor.WHITE}] "

    /**
     * Send pre-defined command syntax with arguments formatting
     *
     * @param commander
     * @param commandSyntax Pre-defined command syntax
     * @param args The arguments
     */
    fun sendCommandSyntax(commander: CommandSender, commandSyntax: CommandSyntax, vararg args: Any) {
        sendCommandSyntax(commander, commandSyntax.syntax, *args)
    }

    /**
     * Send custom command syntax with arguments formatting
     *
     * @param commander
     * @param customSyntax Custom command syntax
     * @param args The arguments
     */
    fun sendCommandSyntax(commander: CommandSender, customSyntax: String, vararg args: Any) {
        sendCommandSyntax(commander, String.format(customSyntax, *args))
    }

    /**
     * Send pre-defined command syntax
     *
     * @param commander
     * @param commandSyntax Pre-defined command syntax
     */
    fun sendCommandSyntax(commander: CommandSender, commandSyntax: CommandSyntax) {
        sendCommandSyntax(commander, commandSyntax.syntax)
    }

    /**
     * Send custom command syntax
     *
     * @param command
     * @param customSyntax
     */
    fun sendCommandSyntax(commander: CommandSender, customSyntax: String) {
        commander.sendMessage("${COMMAND_SYNTAX_PREFIX}$customSyntax")
    }

    /**
     * Alert invalid command argument to commander.
     *
     * @param commander The command executor
     * @param args Command option arguments
     * @param argIndex This can be optional since it useful when mention custom arg placement instead of last arg
     */
    fun alertInvalidCommandArg(commander: CommandSender, args: Array<out String>, argIndex: Int = 0) {
        val actualArgIndex = if (argIndex == 0 && args.isNotEmpty()) args.size - 1 else argIndex

        val arg = args[actualArgIndex]

//        if (actualArgIndex == 0) {
//            return sendCommandSyntax(
//                commander,
//                CommandSyntax.INVALID_ARGUMENT,
//                "${ChatColor.GOLD}/neon ${ChatColor.UNDERLINE}$arg", 2
//            )
//        }

        return sendCommandSyntax(
            commander,
            CommandSyntax.INVALID_ARGUMENT,
            "${ChatColor.GOLD}...${args[actualArgIndex - 1]} ${ChatColor.UNDERLINE}$arg", (actualArgIndex + 2)
        )
    }

    /**
     * Alert invalid command argument to commander.
     *
     * @param commander
     * @param args
     * @param commandOptionArg
     */
    fun alertInvalidCommandArg(commander: CommandSender, args: Array<out String>, commandOptionArg: AbstractCommandOptionArgument) {
        if (args.isEmpty()) return

        val argIndex = commandOptionArg.optionArgIndex

        return sendCommandSyntax(
            commander,
            CommandSyntax.INVALID_ARGUMENT,
            // +2 is a must since the position counting from the start of the command prefix, and the given arg index is counting after the command prefix:
            // /neon {command} {commandOption} {commandOptionArg}
            // Pos 1   Pos 2       Pos 3             Pos 4
            "${ChatColor.GOLD}...${args[argIndex - 1]} ${ChatColor.UNDERLINE}${args[argIndex]}", (argIndex + 2)
        )
    }


    /**
     * Alert invalid command to commander
     *
     * @param commander
     * @param command
     */
    fun alertInvalidCommand(commander: CommandSender, command: String) {
        sendCommandSyntax(commander, CommandSyntax.INVALID_COMMAND, command)
    }
}