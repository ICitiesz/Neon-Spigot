package com.islandstudio.neon.command

import com.islandstudio.neon.command.properties.AccessibleCommand
import org.bukkit.command.CommandSender

interface ICommandDispatcher {
    /**
     * Set the command handler for the particular feature.
     *
     * @param commander The player who perform the command.
     * @param args The command arguments.
     */
    fun getCommandDispatcher(
        commander: CommandSender,
        accessibleCommands: ArrayList<AccessibleCommand>,
        args: Array<out String>
    )

    /**
     * Set the tab completion for the particular command.
     *
     * @param commander The player who perform the command.
     * @param args The command arguments.
     * @return A list of valid command arguments.
     */
    fun getTabCompletion(
        commander: CommandSender,
        accessibleCommand: ArrayList<AccessibleCommand>,
        args: Array<out String>
    ): MutableList<String> {
        return mutableListOf()
    }
}