package com.islandstudio.neon.command

import com.islandstudio.neon.command.properties.AccessibleCommand
import org.bukkit.command.CommandSender

interface ICommandDispatcherNew {
    /**
     * Process command
     *
     * @param commander
     * @param playerAccessibleCommand
     * @param args
     */
    fun processCommand(commander: CommandSender, playerAccessibleCommand: AccessibleCommand?, args: Array<out String>)

    /**
     * Set the tab completion for the particular command.
     *
     * @param commander The command sender who perform the command.
     * @param playerAccessibleCommand The player accessible commands, console command sender can be null
     * @param args The command arguments.
     * @return A list of valid command arguments.
     */
    fun getTabCompletion(commander: CommandSender, playerAccessibleCommand: AccessibleCommand?, args: Array<out String>): MutableList<String> {
        return mutableListOf()
    }
}