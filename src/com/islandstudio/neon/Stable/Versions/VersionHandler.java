package com.islandstudio.neon.Stable.Versions;

import com.islandstudio.neon.MainCore;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

public class VersionHandler {
    private static final Plugin plugin = MainCore.getPlugin(MainCore.class);

    private static final String version = plugin.getServer().getBukkitVersion().split("\\.")[0] + "." + plugin.getServer().getBukkitVersion().split("\\.")[1];
    // 1.15.2-R0.1-SNAPSHOT
    public static void initiate() {
        switch (version) {
            case "1.14": {
                loadConfig();
                System.out.println("This is mc version 1.14!");
                break;
            }

            case "1.15": {
                loadConfig();
                System.out.println("This is mc version 1.15!");
                break;
            }

            case "1.16": {
                loadConfig();

                break;
            }

            default: {
                plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Out of support for Minecraft" + version + "!" + " Please update the plugin to the latest version!");
                break;
            }
        }


        //System.out.println(version);
    }

    private static void loadConfig() {
        plugin.getConfig().options().copyDefaults(true);
        plugin.getConfig().options().copyHeader(true);
        plugin.saveConfig();
    }

    public static void sendIntro() {
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Neon is started!");
    }

    public static void sendOutro() {
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Neon is stopped!");
    }
}
