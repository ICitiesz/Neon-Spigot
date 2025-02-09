package com.islandstudio.neon.stable.command.processing

import org.bukkit.ChatColor

enum class CommandSyntax(val syntax: String) {
    UNSUPPORTED_COMMAND_EXECUTION("${ChatColor.YELLOW}This command doesn't support console execution!"),
    UNSUPPORTED_GUI_ACCESS("${ChatColor.YELLOW}GUI only accessible through in-game command execution!"),
    INVALID_ARGUMENT("${ChatColor.RED}Invalid or missing argument: %s${ChatColor.RESET}${ChatColor.RED} <- at position %d"),
    INVALID_ARGUMENT_WITH_VALUE("${ChatColor.RED}Invalid or missing argument! -> %s"),
    INVALID_PERMISSION("${ChatColor.RED}You don't have permissicon to use this command!"),
    INVALID_RANK("${ChatColor.RED}You need higher rank to use this command!"),
    INVALID_COMMAND(
        "${ChatColor.YELLOW}Sorry, there are no command as " +
                "'${ChatColor.WHITE}%s${ChatColor.YELLOW}'!"
    ),
    INVALID_CONFIRMATION("${ChatColor.RED}Invalid confirmation! Please type as same as '${ChatColor.WHITE}CONFIRM${ChatColor.RED}'!"),
    PLAYER_NOT_FOUND("${ChatColor.RED}The target player, ${ChatColor.WHITE}%s ${ChatColor.RED}not found!")
    ;
}