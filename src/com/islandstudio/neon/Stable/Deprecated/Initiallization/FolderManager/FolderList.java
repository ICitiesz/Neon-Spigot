package com.islandstudio.neon.Stable.Deprecated.Initiallization.FolderManager;

import com.islandstudio.neon.Stable.New.Utilities.NMS_Class_Version;
import com.islandstudio.neon.MainCore;

import java.io.File;

public class FolderList {
    private static Object getDataFolder;

    static {
        try {
            Object plugin = NMS_Class_Version.getBukkitClass("plugin.java.JavaPlugin").getMethod("getPlugin", Class.class).invoke(null, MainCore.class);
            getDataFolder = plugin.getClass().getMethod("getDataFolder").invoke(plugin);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static final File dataFolder = (File) getDataFolder;

    // --------------------------------------- MC 1.14 ----------------------------------------------- //
    public static final File versionFolder_1 = new File(dataFolder, "1_14");

    public static final File onlineFolder_1 = new File(versionFolder_1, "Online_Mode");
    public static final File serverFolder_1_a = new File(onlineFolder_1, "Server_Configurations");
    public static final File serverDataFolder_1_a = new File(onlineFolder_1, "Server_Data");
    public static final File dataFolder_1_a_1 = new File(serverDataFolder_1_a, "Player_Data");
    public static final File dataFolder_1_a_2 = new File(serverDataFolder_1_a, "Chat_Logs");
    public static final File dataFolder_1_a_3 = new File(serverDataFolder_1_a, "Warp_Coordinates");

    public static final File offlineFolder_1 = new File(versionFolder_1, "Offline_Mode");
    public static final File serverFolder_1_b = new File(offlineFolder_1, "Server_Configurations");
    public static final File serverDataFolder_1_b = new File(offlineFolder_1, "Server_Data");
    public static final File dataFolder_1_b_1 = new File(serverDataFolder_1_b, "Player_Data");
    public static final File dataFolder_1_b_2 = new File(serverDataFolder_1_b, "Chat_Logs");
    public static final File dataFolder_1_b_3 = new File(serverDataFolder_1_b, "Warp_Coordinates");
    // ----------------------------------------------------------------------------------------------- //


    // --------------------------------------- MC 1.15 ----------------------------------------------- //
    public static final File versionFolder_2 = new File(dataFolder, "1_15");

    public static final File onlineFolder_2 = new File(versionFolder_2, "Online_Mode");
    public static final File serverFolder_2_a = new File(onlineFolder_2, "Server_Configurations");
    public static final File serverDataFolder_2_a = new File(onlineFolder_2, "Server_Data");
    public static final File dataFolder_2_a_1 = new File(serverDataFolder_2_a, "Player_Data");
    public static final File dataFolder_2_a_2 = new File(serverDataFolder_2_a, "Chat_Logs");
    public static final File dataFolder_2_a_3 = new File(serverDataFolder_2_a, "Warp_Coordinates");

    public static final File offlineFolder_2 = new File(versionFolder_2, "Offline_Mode");
    public static final File serverFolder_2_b = new File(offlineFolder_2, "Server_Configurations");
    public static final File serverDataFolder_2_b = new File(offlineFolder_2, "Server_Data");
    public static final File dataFolder_2_b_1 = new File(serverDataFolder_2_b, "Player_Data");
    public static final File dataFolder_2_b_2 = new File(serverDataFolder_2_b, "Chat_Logs");
    public static final File dataFolder_2_b_3 = new File(serverDataFolder_2_b, "Warp_Coordinates");
    // ----------------------------------------------------------------------------------------------- //


    // --------------------------------------- MC 1.16 ----------------------------------------------- //
    public static final File versionFolder_3 = new File(dataFolder, "1_16");

    public static final File onlineFolder_3 = new File(versionFolder_3, "Online_Mode");
    public static final File serverFolder_3_a = new File(onlineFolder_3, "Server_Configurations");
    public static final File serverDataFolder_3_a = new File(onlineFolder_3, "Server_Data");
    public static final File dataFolder_3_a_1 = new File(serverDataFolder_3_a, "Player_Data");
    public static final File dataFolder_3_a_2 = new File(serverDataFolder_3_a, "Chat_Logs");
    public static final File dataFolder_3_a_3 = new File(serverDataFolder_3_a, "Warp_Coordinates");

    public static final File offlineFolder_3 = new File(versionFolder_3, "Offline_Mode");
    public static final File serverFolder_3_b = new File(offlineFolder_3, "Server_Configurations");
    public static final File serverDataFolder_3_b = new File(offlineFolder_3, "Server_Data");
    public static final File dataFolder_3_b_1 = new File(serverDataFolder_3_b, "Player_Data");
    public static final File dataFolder_3_b_2 = new File(serverDataFolder_3_b, "Chat_Logs");
    public static final File dataFolder_3_b_3 = new File(serverDataFolder_3_b, "Warp_Coordinates");
    // ----------------------------------------------------------------------------------------------- //
}
