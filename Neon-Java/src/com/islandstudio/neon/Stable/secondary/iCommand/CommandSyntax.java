package com.islandstudio.neon.stable.secondary.iCommand;

import org.bukkit.ChatColor;

public enum CommandSyntax {
    INVALID_ARGUMENT(ICommand.COLORED_PLUGIN_NAME + ChatColor.RED + "Invalid or missing argument! Please type '/neon' to show available command syntax!"),
    INVALID_PERMISSION(ICommand.COLORED_PLUGIN_NAME + ChatColor.RED + "You don't have permission to use this command!");

    private final String syntaxMessage;

    CommandSyntax(String syntaxMessage) {
        this.syntaxMessage = syntaxMessage;
    }

    public String getSyntaxMessage() {
        return syntaxMessage;
    }

    public static class Handler {
        public static String createSyntaxMessage(String message) {
            return ICommand.COLORED_PLUGIN_NAME + message;
        }
    }
}
