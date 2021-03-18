package com.islandstudio.neon.Stable.Deprecated.Utilities;

import com.islandstudio.neon.Stable.Old.Initialization.FolderManager.FolderList;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class PlayerDataHandler {
    private static FileConfiguration fileConfiguration;

    public static void setup(Player player) {
        if (Bukkit.getServer().getOnlineMode()) {
            File file = new File(FolderList.getFolder_2a_1, player.getUniqueId() + ".sv");

            if (!file.exists()) {
                try {
                    boolean isFileCreated = file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                fileConfiguration = YamlConfiguration.loadConfiguration(file);

                if (player.isOp()) {
                    fileConfiguration.addDefault("Name", player.getName());
                    fileConfiguration.addDefault("UUID", player.getUniqueId().toString());
                    fileConfiguration.addDefault("Rank", "OWNER");
                    fileConfiguration.addDefault("isMuted", "false");
                } else {
                    fileConfiguration.addDefault("Name", player.getName());
                    fileConfiguration.addDefault("UUID", player.getUniqueId().toString());
                    fileConfiguration.addDefault("Rank", "MEMBER");
                    fileConfiguration.addDefault("isMuted", "false");
                }

                fileConfiguration.options().copyDefaults(true);

                try {
                    fileConfiguration.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            File file = new File(FolderList.getFolder_2b_1, player.getUniqueId() + ".sv");

            if (!file.exists()) {
                try {
                    boolean isFileCreated = file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                fileConfiguration = YamlConfiguration.loadConfiguration(file);

                if (player.isOp()) {
                    fileConfiguration.addDefault("Name", player.getName());
                    fileConfiguration.addDefault("UUID", player.getUniqueId().toString());
                    fileConfiguration.addDefault("Rank", "OWNER");
                    fileConfiguration.addDefault("isMuted", "false");
                } else {
                    fileConfiguration.addDefault("Name", player.getName());
                    fileConfiguration.addDefault("UUID", player.getUniqueId().toString());
                    fileConfiguration.addDefault("Rank", "MEMBER");
                    fileConfiguration.addDefault("isMuted", "false");
                }

                fileConfiguration.options().copyDefaults(true);

                try {
                    fileConfiguration.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static FileConfiguration getData(File dataFile) {
        fileConfiguration = YamlConfiguration.loadConfiguration(dataFile);
        return fileConfiguration;
    }

    public static FileConfiguration setData(File dataFile) {
        fileConfiguration = YamlConfiguration.loadConfiguration(dataFile);
        return fileConfiguration;
    }

    public static void saveData(File dataFile) {
        try {
            fileConfiguration.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        fileConfiguration = YamlConfiguration.loadConfiguration(dataFile);
    }

    public static File getDataFile(Player player) {
        if (Bukkit.getServer().getOnlineMode()) {
            return new File(FolderList.getFolder_2a_1, player.getUniqueId() + ".sv");
        } else {
            return new File(FolderList.getFolder_2b_1, player.getUniqueId() + ".sv");
        }
    }
}
