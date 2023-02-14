package com.islandstudio.neon.stable.primary.iConstructor;

import com.islandstudio.neon.Neon;
import com.islandstudio.neon.stable.primary.iExperimental.IExperimental;
import com.islandstudio.neon.stable.primary.iFolder.IFolder;
import com.islandstudio.neon.stable.primary.iServerConfig.IServerConfig;
import com.islandstudio.neon.stable.secondary.iCommand.ICommand;
import com.islandstudio.neon.stable.secondary.iCutter.ICutter;
import com.islandstudio.neon.stable.secondary.iEffect.IEffect;
import com.islandstudio.neon.stable.secondary.iHarvest.IHarvest;
import com.islandstudio.neon.stable.secondary.iModerator.IModerator;
import com.islandstudio.neon.stable.secondary.iPVP.IPVP;
import com.islandstudio.neon.stable.secondary.iProfile.IProfile;
import com.islandstudio.neon.stable.secondary.iRank.IRank;
import com.islandstudio.neon.stable.secondary.iServerConstantProcessor.IServerConstantProcessor;
import com.islandstudio.neon.stable.secondary.iSmelter.ISmelter;
import com.islandstudio.neon.stable.secondary.iWaypoints.IWaypoints;
import com.islandstudio.neon.stable.utils.iGUI.ButtonHighlighter;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public final class IConstructor {
    private static final Plugin PLUGIN = Neon.getPlugin(Neon.class);
    private static final String RAW_VERSION = PLUGIN.getServer().getBukkitVersion().split("-")[0];
    private static final String VERSION = RAW_VERSION.split("\\.")[0] + "." + RAW_VERSION.split("\\.")[1];

    /**
     * Initialize primary components for the plugin.
     */
    public static void buildPrimary() {
        switch (VERSION) {
            case "1.14":
            case "1.15":
            case "1.16": {
                /* Primary Component */
                IFolder.Handler.init();
                IServerConfig.Handler.init();
                IExperimental.Handler.init();
                ButtonHighlighter.Handler.init();
                break;
            }

            default: {
                try {
                    Thread.sleep(1500);
                    PLUGIN.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[Neon] " + ChatColor.RED + "Incompatible Minecraft version! Pre-initialization cancelled!");
                } catch (InterruptedException err) {
                    System.out.println("An error occurred while trying to construct Neon plugin: Thread interrupted!");
                }
            }
        }
    }

    /**
     * Initialize secondary components for the plugin.
     */
    public static void buildSecondary() {
        switch (VERSION) {
            case "1.14":
            case "1.15":
            case "1.16": {
                try {
                    PLUGIN.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[Neon] " + ChatColor.YELLOW + "Detected Minecraft " + ChatColor.GREEN + RAW_VERSION + ChatColor.YELLOW + "!");
                    Thread.sleep(1000);
                    PLUGIN.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[Neon] " + ChatColor.YELLOW + "Initializing features for Minecraft " + ChatColor.GREEN + RAW_VERSION + ChatColor.YELLOW + " ......");

                    /* Secondary Component */
                    IExperimental.Handler.initEvent();
                    ICommand.Handler.init();
                    IProfile.Handler.init();
                    IServerConstantProcessor.Handler.init();
                    IModerator.Handler.init();

                    IHarvest.Handler.init();
                    ICutter.Handler.init();
                    ISmelter.Handler.init();
                    IWaypoints.Handler.init();
                    IPVP.Handler.init();
                    IRank.Handler.init();
                    IEffect.Handler.init();

                    Thread.sleep(2500);
                    PLUGIN.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[Neon] " + ChatColor.GREEN + "Initialization complete!");
                    sendIntro();
                } catch (InterruptedException err) {
                    System.out.println("An error occurred while trying to construct Neon plugin: Thread interrupted!");
                }

                break;
            }

            default: {
                try {
                    final String SUPPORTED_VERSION = "1.14.X ~ 1.16.X";

                    Thread.sleep(1500);
                    PLUGIN.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[Neon] " + ChatColor.RED + "Incompatible Minecraft version! Post-initialization cancelled!");
                    PLUGIN.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[Neon] " + ChatColor.YELLOW + "Please check for the compatible Minecraft version!");
                    PLUGIN.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[Neon] " + ChatColor.YELLOW + "Supported Minecraft version: " + ChatColor.GREEN + SUPPORTED_VERSION);
                } catch (InterruptedException err) {
                    System.out.println("An error occurred while trying to construct Neon plugin: Thread interrupted!");
                }

                break;
            }
        }
    }

    public static void sendIntro() {
        PLUGIN.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|+++++++++++++++++=================+++++++++++++++++|");
        PLUGIN.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|-----------------=================-----------------|");
        PLUGIN.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|-----------------== Neon v1.9.2 ==-----------------|");
        PLUGIN.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|-----------------===" + ChatColor.GREEN + " <Started> " + ChatColor.GOLD + "===-----------------|");
        PLUGIN.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|-----------------=================-----------------|");
        PLUGIN.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|+++++++++++++++++=================+++++++++++++++++|");
    }

    public static void sendOutro() {
        PLUGIN.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|+++++++++++++++++=================+++++++++++++++++|");
        PLUGIN.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|-----------------=================-----------------|");
        PLUGIN.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|-----------------== Neon v1.9.2 ==-----------------|");
        PLUGIN.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|-----------------===" + ChatColor.RED + " <Stopped> " + ChatColor.GOLD + "===-----------------|");
        PLUGIN.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|-----------------=================-----------------|");
        PLUGIN.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|+++++++++++++++++=================+++++++++++++++++|");
    }

    public static String getVersion() {
        return VERSION;
    }

    public static Plugin getPlugin() {
        return PLUGIN;
    }

    /***
     * Enable event. This is used when the server is starting up and only if the particular server config is toggled.
     */
    public static void enableEvent(Listener eventListener) {
        HandlerList.getRegisteredListeners(PLUGIN).forEach(event -> {
            if (event.getListener().getClass().getCanonicalName().equalsIgnoreCase(eventListener.getClass().getCanonicalName())) return;
        });

        PLUGIN.getServer().getPluginManager().registerEvents(eventListener, PLUGIN);
    }

    /***
     * Disable event. This is used when certain server config is disabled.
     */
    public static void disableEvent(Listener eventListener) {
        HandlerList.getRegisteredListeners(PLUGIN).forEach(event -> {
            if (event.getListener().getClass().getCanonicalName().equalsIgnoreCase(eventListener.getClass().getCanonicalName())) {
                HandlerList.unregisterAll(event.getListener());
            }
        });
    }
}
