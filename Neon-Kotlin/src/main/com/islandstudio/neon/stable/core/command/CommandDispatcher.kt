package com.islandstudio.neon.stable.core.command

import org.bukkit.command.CommandSender

interface CommandDispatcher {
    /**
     * Set the command handler for the particular feature.
     *
     * @param commander The player who perform the command.
     * @param args The command arguments.
     */
    fun getCommandDispatcher(commander: CommandSender, args: Array<out String>)

    /**
     * Set the tab completion for the particular command.
     *
     * @param commander The player who perform the command.
     * @param args The command arguments.
     * @return A list of valid command arguments.
     */
    fun getTabCompletion(commander: CommandSender, args: Array<out String>): MutableList<String> {
        return mutableListOf()
    }
}