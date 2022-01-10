package com.islandstudio.neon.stable.primary.nCommand

import org.bukkit.ChatColor

enum class CommandSyntax(val syntaxMessage: String) {
    INVALID_ARGUMENT("${NCommand.getPluginName()}${ChatColor.RED}Invalid or missing argument!"),
    INVALID_PERMISSION("${NCommand.getPluginName()}${ChatColor.RED}You don't have permission to use this command!"),
    INVALID_RANK("${NCommand.getPluginName()}${ChatColor.RED}You need higher rank to use this command!");

    companion object {
        /* Create custom syntax message. */
        fun createSyntaxMessage(syntaxMessage: String): String {
            return "${NCommand.getPluginName()}$syntaxMessage"
        }
    }
}

