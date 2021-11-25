package com.islandstudio.neon.Stable.Old.Initialization.FolderManager;

import com.islandstudio.neon.MainCore;
import org.bukkit.plugin.Plugin;

import java.io.File;

public final class FolderList {
    private static final Plugin getPlugin = MainCore.getPlugin(MainCore.class);

    public static final File getFolder_1 = new File(getPlugin.getDataFolder(), "Server_Settings");
    public static final File getFolder_2 = new File(getPlugin.getDataFolder(), "Server_Data");

    public static final File getFolder_1_a = new File(getFolder_1, "Online_Mode");
    public static final File getFolder_1_b = new File(getFolder_1, "Offline_Mode");

    public static final File getFolder_2a = new File(getFolder_2, "Online_Mode");
    public static final File getFolder_2a_1 = new File(getFolder_2a, "Players_Data");
    public static final File getFolder_2a_2 = new File(getFolder_2a, "Chat_Logs");
    public static final File getFolder_2a_3 = new File(getFolder_2a, "Warp_Coordinates");

    public static final File getFolder_2b = new File(getFolder_2, "Offline_Mode");
    public static final File getFolder_2b_1 = new File(getFolder_2b, "Players_Data");
    public static final File getFolder_2b_2 = new File(getFolder_2b, "Chat_Logs");
    public static final File getFolder_2b_3 = new File(getFolder_2b, "Warp_Coordinates");
}
