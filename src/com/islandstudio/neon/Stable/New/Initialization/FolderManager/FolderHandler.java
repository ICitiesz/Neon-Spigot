package com.islandstudio.neon.Stable.New.Initialization.FolderManager;


import com.islandstudio.neon.MainCore;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;

public class FolderHandler {
    private static final Plugin plugin = MainCore.getPlugin(MainCore.class);

    private static final File DATA_FOLDER = plugin.getDataFolder();
    private static final File BETA_FOLDER = new File(DATA_FOLDER, "Beta_Folder");

    private static final String VERSION = (plugin.getServer().getBukkitVersion().split("-")[0]).split("\\.")[0] + "." + (plugin.getServer().getBukkitVersion().split("-")[0]).split("\\.")[1];

    /* Initialization for Folder Handler */
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

    /* Get the directory of Neon which inside the plugin directory witin the Spigot server directory. */
    public static File getDataFolder() {
        return DATA_FOLDER;
    }

    /* Get the Spigot version which is required for creating folder for each individual version. */
    public static String getVersion() {
        return VERSION.replace(".", "_");
    }

    /* Get the mode, either Online Mode or Offline Mode */
    public static String getMode() {
        if (plugin.getServer().getOnlineMode()) {
            return "Online_Mode";
        } else {
            return "Offline_Mode";
        }
    }
}
