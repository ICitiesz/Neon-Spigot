package com.islandstudio.neon.Stable.New.Utilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ServerHandler {
    public static void broadcastJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Server server = player.getServer();

        e.setJoinMessage("");

        server.broadcastMessage(ChatColor.GOLD + "Welcome back, " + ChatColor.GREEN + player.getName() + ChatColor.GOLD + "!");
        server.broadcastMessage(ChatColor.GREEN + "" + Bukkit.getOnlinePlayers().size() + ChatColor.GOLD + " of " + ChatColor.RED + Bukkit.getMaxPlayers() + ChatColor.GOLD + " player(s) Online!");
    }

    public static void broadcastQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Server server = player.getServer();

        int onlinePlayers = server.getOnlinePlayers().size();

        e.setQuitMessage("");

        server.broadcastMessage(ChatColor.GREEN + player.getName() + ChatColor.GOLD + " left," + ChatColor.GREEN + " " + (onlinePlayers - 1) + ChatColor.GOLD + " other(s) here!");
    }
}
