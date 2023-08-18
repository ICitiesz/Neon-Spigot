package com.islandstudio.neon.stable.primary.nCommand

import org.bukkit.entity.Player

interface CommandHandler {
    /**
     * Set the command handler for the particular feature.
     *
     * @param commander The player who perform the command.
     * @param args The command arguments.
     */
    fun setCommandHandler(commander: Player, args: Array<out String>)

    /**
     * Set the tab completion for the particular command.
     *
     * @param commander The player who perform the command.
     * @param args The command arguments.
     * @return A list of valid command arguments.
     */
     fun tabCompletion(commander: Player, args: Array<out String>): MutableList<String> {
        return mutableListOf()
    }
}