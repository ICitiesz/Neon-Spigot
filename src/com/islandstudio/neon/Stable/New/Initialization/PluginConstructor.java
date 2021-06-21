package com.islandstudio.neon.Stable.New.Initialization;

import com.islandstudio.neon.Experimental.Commands.CommandHandler;
import com.islandstudio.neon.Experimental.iCutter.ICutter;
import com.islandstudio.neon.Experimental.iSmelter.ISmelter;
import com.islandstudio.neon.Stable.New.GUI.Interfaces.iWaypoints.IWaypoints;
import com.islandstudio.neon.Stable.New.Initialization.FolderManager.FolderHandler;
import com.islandstudio.neon.Experimental.PVPHandler;
import com.islandstudio.neon.Stable.New.Utilities.ServerCFGHandler;
import com.islandstudio.neon.MainCore;
import com.islandstudio.neon.Stable.New.PluginFeatures.RankSystem.RankTags;
import com.islandstudio.neon.Stable.New.Event.EventCore;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

public final class PluginConstructor {
    private static final Plugin plugin = MainCore.getPlugin(MainCore.class);
    private static final String VERSION = plugin.getServer().getBukkitVersion().split("\\.")[0] + "." + plugin.getServer().getBukkitVersion().split("\\.")[1];
    private static final String RAW_VERSION = plugin.getServer().getBukkitVersion().split("-")[0];

    public static void start() throws Exception {
        switch (VERSION) {
            case "1.14":

            case "1.15":

            case "1.16": {
                plugin.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[Neon] " + ChatColor.YELLOW + "Detected Minecraft " + ChatColor.GREEN + RAW_VERSION + ChatColor.YELLOW + "!");
                Thread.sleep(1000);
                plugin.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[Neon] " + ChatColor.YELLOW + "Initializing features for Minecraft " + ChatColor.GREEN + VERSION + ChatColor.YELLOW + "......");

                eventRegister();
                FolderHandler.init();
                ServerCFGHandler.init();
                ICutter.init();
                ISmelter.init();
                IWaypoints.init();
                CommandHandler.init();
                PVPHandler.init();
                RankTags.init();
                //ChatLogger.initialize();
                loadConfig();

                Thread.sleep(2500);
                plugin.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[Neon] " + ChatColor.GREEN + "Initialization complete!");
                sendIntro();
                break;
            }

            default: {
                final String SUPPORTED_VERSION = "1.14 ~ 1.16";

                plugin.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[Neon] " + ChatColor.RED + "Incompatible Minecraft version! Please check for the latest version!");
                plugin.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[Neon] " + ChatColor.YELLOW + "Supported version: " + ChatColor.GREEN + SUPPORTED_VERSION);
                break;
            }
        }
    }

    public static void sendIntro() {
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|+++++++++++++++++=================+++++++++++++++++|");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|-----------------=================-----------------|");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|---------------== Neon v1.9-pre_1 ==---------------|");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|-----------------===" + ChatColor.GREEN + " <Started> " + ChatColor.GOLD + "===-----------------|");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|-----------------=================-----------------|");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|+++++++++++++++++=================+++++++++++++++++|");
    }

    public static void sendOutro() {
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|+++++++++++++++++=================+++++++++++++++++|");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|-----------------=================-----------------|");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|---------------== Neon v1.9-pre_1 ==----------------|");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|-----------------===" + ChatColor.RED + " <Stopped> " + ChatColor.GOLD + "===-----------------|");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|-----------------=================-----------------|");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|+++++++++++++++++=================+++++++++++++++++|");
    }

    private static void loadConfig() {
        plugin.getConfig().options().copyDefaults(true);
        plugin.getConfig().options().copyHeader(true);
        plugin.saveConfig();
    }

    private static void eventRegister() {
        plugin.getServer().getPluginManager().registerEvents(new EventCore(), plugin);
    }
}
