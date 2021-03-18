package com.islandstudio.neon.Stable.New.Initialization.FolderManager;


import com.islandstudio.neon.Stable.New.Utilities.NMS_Class_Version;
import com.islandstudio.neon.MainCore;

import java.io.File;
import java.util.ArrayList;

public class FolderHandler {
    private static Object getBukkitVersion;
    private static Object getOnlineMode;
    private static Object getDataFolder;

    static {
        try {
            Object plugin = NMS_Class_Version.getBukkitClass("plugin.java.JavaPlugin").getMethod("getPlugin", Class.class).invoke(null, MainCore.class);
            Object getServer = plugin.getClass().getMethod("getServer").invoke(plugin);

            getDataFolder = plugin.getClass().getMethod("getDataFolder").invoke(plugin);
            getOnlineMode = getServer.getClass().getMethod("getOnlineMode").invoke(getServer);
            getBukkitVersion = getServer.getClass().getMethod("getBukkitVersion").invoke(getServer);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static final File DATA_FOLDER = (File) getDataFolder;
    private static final File BETA_FOLDER = new File(DATA_FOLDER, "Beta_Folder");

    private static final String VERSION = ((String) getBukkitVersion).split("\\.")[0] + "." + ((String) getBukkitVersion).split("\\.")[1];
    private static final boolean ONLINE_MODE = (boolean) getOnlineMode;

    public static void init() {
        ArrayList<File> folders = new ArrayList<>();

        for (FolderList folderList : FolderList.values()) {
            folders.add(folderList.getFolder());
        }

        for (File folder : folders) {
            if (!folder.exists()) {
                boolean createFile = folder.mkdirs();
            }
        }

        folders.clear();
    }

    public static File getBetaFolder() {
        return BETA_FOLDER;
    }

    public static File getDataFolder() {
        return DATA_FOLDER;
    }

    public static String getVersion() {
        return VERSION.replace(".", "_");
    }

    public static String getMode() {
        if (ONLINE_MODE) {
            return "Online_Mode";
        } else {
            return "Offline_Mode";
        }
    }
}
