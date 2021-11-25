package com.islandstudio.neon.Stable.Old.Initialization.FolderManager;

import com.islandstudio.neon.MainCore;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class FolderHandler {
    private final static Plugin getPlugin = MainCore.getPlugin(MainCore.class);

    private static final ArrayList<String> notification = new ArrayList<>();

    public static void initialize() {
        if (!getPlugin.getDataFolder().exists()) {
            boolean isDataFolderCreated = getPlugin.getDataFolder().mkdir();

            if (isDataFolderCreated) {
                System.out.println("Folder \"\\" + getPlugin.getDataFolder().getName() + "\"\\ has been created!");
            } else {
                System.out.println("Failed to created folder \"\\" + getPlugin.getDataFolder().getName() + "\"\\!");
            }
        }

        /* ----------- folder_1 & folder_2 initialization ----------- */
        if (!FolderList.getFolder_1.exists()) {
            boolean isFolder_1_Created = FolderList.getFolder_1.mkdir();

            if (!isFolder_1_Created) {
                notification.add(ChatColor.RED + "Failed to created folder" + ChatColor.GRAY + "\"" + ChatColor.WHITE + FolderList.getFolder_1.getName() + ChatColor.GRAY + "\"" + ChatColor.RED + "!");
            }
        }

        if (!FolderList.getFolder_2.exists()) {
            boolean isFolder_2_Created = FolderList.getFolder_2.mkdir();

            if (!isFolder_2_Created) {
                notification.add(ChatColor.RED + "Failed to created folder" + ChatColor.GRAY + "\"" + ChatColor.WHITE + FolderList.getFolder_2.getName() + ChatColor.GRAY + "\"" + ChatColor.RED + "!");
            }
        }

        /* ------------- */
        if (!FolderList.getFolder_1_b.exists()) {
            boolean isFolder_1_b_Created = FolderList.getFolder_1_b.mkdir();
        }

        if (!FolderList.getFolder_1_a.exists()) {
            boolean isFolder_1_a_Created = FolderList.getFolder_1_a.mkdir();
        }


        /* ----------- folder_2a & child initialization ----------- */
        if (!FolderList.getFolder_2a.exists()) {
            boolean isFolder_2a_Created = FolderList.getFolder_2a.mkdir();

            if (!isFolder_2a_Created) {
                notification.add(ChatColor.RED + "Failed to created folder" + ChatColor.GRAY + "\"" + ChatColor.WHITE + FolderList.getFolder_2a.getName() + ChatColor.GRAY + "\"" + ChatColor.RED + "!");
            }
        }

        if (!FolderList.getFolder_2a_1.exists()) {
            boolean isFolder_2a_1_Created = FolderList.getFolder_2a_1.mkdir();

            if (!isFolder_2a_1_Created) {
                notification.add(ChatColor.RED + "Failed to created folder" + ChatColor.GRAY + "\"" + ChatColor.WHITE + FolderList.getFolder_2a_1.getName() + ChatColor.GRAY + "\"" + ChatColor.RED + "!");
            }
        }

        if (!FolderList.getFolder_2a_2.exists()) {
            boolean isFolder_2a_2_Created = FolderList.getFolder_2a_2.mkdir();

            if (!isFolder_2a_2_Created) {
                notification.add(ChatColor.RED + "Failed to created folder" + ChatColor.GRAY + "\"" + ChatColor.WHITE + FolderList.getFolder_2a_2.getName() + ChatColor.GRAY + "\"" + ChatColor.RED + "!");
            }
        }

        /* ----------- folder_2b & child initialization ----------- */
        if (!FolderList.getFolder_2b.exists()) {
            boolean isFolder_2b_Created = FolderList.getFolder_2b.mkdir();

            if (!isFolder_2b_Created) {
                notification.add(ChatColor.RED + "Failed to created folder" + ChatColor.GRAY + "\"" + ChatColor.WHITE + FolderList.getFolder_2b.getName() + ChatColor.GRAY + "\"" + ChatColor.RED + "!");
            }
        }

        if (!FolderList.getFolder_2b_1.exists()) {
            boolean isFolder_2b_1_Created = FolderList.getFolder_2b_1.mkdir();

            if (!isFolder_2b_1_Created) {
                notification.add(ChatColor.RED + "Failed to created folder" + ChatColor.GRAY + "\"" + ChatColor.WHITE + FolderList.getFolder_2b_1.getName() + ChatColor.GRAY + "\"" + ChatColor.RED + "!");
            }
        }

        if (!FolderList.getFolder_2b_2.exists()) {
            boolean isFolder_2b_2_Created = FolderList.getFolder_2b_2.mkdir();

            if (!isFolder_2b_2_Created) {
                notification.add(ChatColor.RED + "Failed to created folder" + ChatColor.GRAY + "\"" + ChatColor.WHITE + FolderList.getFolder_2b_2.getName() + ChatColor.GRAY + "\"" + ChatColor.RED + "!");
            }
        }

        if (!FolderList.getFolder_2b_3.exists()) {
            boolean isFolder_2b_3_Created = FolderList.getFolder_2b_3.mkdir();

            if (!isFolder_2b_3_Created) {
                notification.add(ChatColor.RED + "Failed to created folder" + ChatColor.GRAY + "\"" + ChatColor.WHITE + FolderList.getFolder_2b_3.getName() + ChatColor.GRAY + "\"" + ChatColor.RED + "!");
            }
        }

        /* Status Checker */
        if (notification.size() > 0) {
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~Initialization Error~~~~~~~~~~~~~~~~~~~~~~");
            for (String notify : notification) {
                System.out.println(notify);
            }
            notification.clear();
        }

    }
}
