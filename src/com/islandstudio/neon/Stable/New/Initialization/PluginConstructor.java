package com.islandstudio.neon.Stable.New.Initialization;

import com.islandstudio.neon.Experimental.Commands.CommandHandler;
import com.islandstudio.neon.Experimental.Events;
import com.islandstudio.neon.Stable.New.GUI.Interfaces.iWaypoints.IWaypoints;
import com.islandstudio.neon.Stable.New.Initialization.FolderManager.FolderHandler;
import com.islandstudio.neon.Stable.New.Utilities.NMS_Class_Version;
import com.islandstudio.neon.Experimental.PVPHandler;
import com.islandstudio.neon.Stable.New.Utilities.ServerCfgHandler;
import com.islandstudio.neon.MainCore;
import com.islandstudio.neon.Stable.New.PluginFeatures.RankSystem.RankTags;
import com.islandstudio.neon.Stable.New.Event.EventFunctions;
import org.bukkit.ChatColor;

public final class PluginConstructor {
    private static Object plugin;
    private static Object getPluginManager;
    private static Object getBukkitVersion;
    private static Object getConsoleSender;

    private static Class<?> listener;
    private static Class<?> pluginClass;

    static {
        try {
            plugin = NMS_Class_Version.getBukkitClass("plugin.java.JavaPlugin").getMethod("getPlugin", Class.class).invoke(null, MainCore.class);
            Object getServer = plugin.getClass().getMethod("getServer").invoke(plugin);
            getPluginManager = getServer.getClass().getMethod("getPluginManager").invoke(getServer);
            getBukkitVersion = getServer.getClass().getMethod("getBukkitVersion").invoke(getServer);
            getConsoleSender = getServer.getClass().getMethod("getConsoleSender").invoke(getServer);
            listener = NMS_Class_Version.getBukkitClass("event.Listener");
            pluginClass = NMS_Class_Version.getBukkitClass("plugin.Plugin");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String VERSION = ((String) getBukkitVersion).split("\\.")[0] + "." + ((String) getBukkitVersion).split("\\.")[1];
    private static final String RAW_VERSION = ((String) getBukkitVersion).split("-")[0];

    public static void start() throws Exception {
        switch (VERSION) {
            case "1.14":

            case "1.15":

            case "1.16": {
                getConsoleSender.getClass().getMethod("sendMessage", String.class).invoke(getConsoleSender, ChatColor.YELLOW + "Detected Minecraft " + ChatColor.GREEN + RAW_VERSION + ChatColor.YELLOW + "!");
                //Thread.sleep(1000);
                getConsoleSender.getClass().getMethod("sendMessage", String.class).invoke(getConsoleSender, ChatColor.YELLOW + "Initializing features for Minecraft " + ChatColor.GREEN + VERSION + ChatColor.YELLOW + "......");

                eventRegister();
                FolderHandler.init();
                ServerCfgHandler.init();
                IWaypoints.init();
                CommandHandler.initialize(); //--DONE
                //CommandHandler.initializeBeta();
                PVPHandler.initialize(); //--DONE
                RankTags.initialize(); //-- DONE
                //ChatLogger.initialize();

                //Thread.sleep(2500);
                getConsoleSender.getClass().getMethod("sendMessage", String.class).invoke(getConsoleSender, ChatColor.GREEN + "Initialization complete!");
                break;
            }

            default: {
                final String SUPPORTED_VERSION = "1.14 ~ 1.16";

                System.out.println("Incompatible Minecraft version! Please check for the latest version!");
                System.out.println("Supported version: " + SUPPORTED_VERSION);
                break;
            }
        }
    }


    static void eventRegister() throws Exception {
        getPluginManager.getClass().getDeclaredMethod("registerEvents", listener, pluginClass).invoke(getPluginManager, EventFunctions.class.newInstance(), plugin);
        getPluginManager.getClass().getDeclaredMethod("registerEvents", listener, pluginClass).invoke(getPluginManager, Events.class.newInstance(), plugin);
    }
}
