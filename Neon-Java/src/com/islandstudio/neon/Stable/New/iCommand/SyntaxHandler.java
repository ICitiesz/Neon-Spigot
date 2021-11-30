package com.islandstudio.neon.Stable.New.iCommand;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class SyntaxHandler {
    public static void sendSyntax(Player player, int syntaxIndex) {
        if (syntaxIndex == 1) {
            player.sendRawMessage(ChatColor.RED + "Invalid argument!");
        } else if (syntaxIndex == 2) {
            player.sendRawMessage(ChatColor.RED + "You do not have permission to do that!");
        } else if (syntaxIndex == 3) {
            player.sendRawMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "DEBUG" + ChatColor.WHITE + "]" + ChatColor.YELLOW + " ");
        }
    }

}
