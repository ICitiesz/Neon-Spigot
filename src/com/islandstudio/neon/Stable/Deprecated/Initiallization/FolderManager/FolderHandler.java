package com.islandstudio.neon.Stable.Deprecated.Initiallization.FolderManager;

import com.islandstudio.neon.Stable.New.Utilities.NamespaceVersion;
import com.islandstudio.neon.MainCore;

import java.io.File;

public class FolderHandler {
    private static Object getDataFolder;
    private static Object getBukkitVersion;
    private static Object getOnlineMode;

    static {
        try {
            Object plugin = NamespaceVersion.getBukkitClass("plugin.java.JavaPlugin").getMethod("getPlugin", Class.class).invoke(null, MainCore.class);
            getDataFolder = plugin.getClass().getMethod("getDataFolder").invoke(plugin);
            Object getServer = plugin.getClass().getMethod("getServer").invoke(plugin);
            getBukkitVersion = getServer.getClass().getMethod("getBukkitVersion").invoke(getServer);
            getOnlineMode = getServer.getClass().getMethod("getOnlineMode").invoke(getServer);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static final File dataFolder = (File) getDataFolder;
    private static final String version = ((String) getBukkitVersion).split("\\.")[0] + "." + ((String) getBukkitVersion).split("\\.")[1];
    private static final boolean onlineMode = (boolean) getOnlineMode;

    public static void initialize() {
        if (!dataFolder.exists()) {
            boolean isDataFolderCreated = dataFolder.mkdir();

            if (isDataFolderCreated) {
                System.out.println("Folder \"\\" + dataFolder.getName() + "\"\\ has been created!");
            } else {
                System.out.println("Failed to created folder \"\\" + dataFolder.getName() + "\"\\!");
            }
        }

        switch (version) {
            case "1.14": {
                if (!FolderList.versionFolder_1.exists()) {
                    boolean isVersionFolder_1_Created = FolderList.versionFolder_1.mkdir();
                }

                if (onlineMode) {
                    if (!FolderList.onlineFolder_1.exists()) {
                        boolean isOnlineFolder_1_Created = FolderList.onlineFolder_1.mkdir();
                    }

                    if (!FolderList.serverFolder_1_a.exists()) {
                        boolean isServerFolder_1_a_Created = FolderList.serverFolder_1_a.mkdir();
                    }

                    if (!FolderList.serverDataFolder_1_a.exists()) {
                        boolean isServerDataFolder_1_a_Created = FolderList.serverDataFolder_1_a.mkdir();
                    }

                    if (!FolderList.dataFolder_1_a_1.exists()) {
                        boolean isDataFolder_1_a_1_Created = FolderList.dataFolder_1_a_1.mkdir();
                    }

                    if (!FolderList.dataFolder_1_a_2.exists()) {
                        boolean isDataFolder_1_a_2_Created = FolderList.dataFolder_1_a_2.mkdir();
                    }

                    if (!FolderList.dataFolder_1_a_3.exists()) {
                        boolean isDataFolder_1_a_3_Created = FolderList.dataFolder_1_a_3.mkdir();
                    }
                } else {
                    if (!FolderList.offlineFolder_1.exists()) {
                        boolean isOfflineFolder_1_Created = FolderList.offlineFolder_1.mkdir();
                    }

                    if (!FolderList.serverFolder_1_b.exists()) {
                        boolean isServerFolder_1_b_Created = FolderList.serverFolder_1_b.mkdir();
                    }

                    if (!FolderList.serverDataFolder_1_b.exists()) {
                        boolean isServerDataFolder_1_b_Created = FolderList.serverDataFolder_1_b.mkdir();
                    }

                    if (!FolderList.dataFolder_1_b_1.exists()) {
                        boolean isDataFolder_1_b_1_Created = FolderList.dataFolder_1_b_1.mkdir();
                    }

                    if (!FolderList.dataFolder_1_b_2.exists()) {
                        boolean isDataFolder_1_b_2_Created = FolderList.dataFolder_1_b_2.mkdir();
                    }

                    if (!FolderList.dataFolder_1_b_3.exists()) {
                        boolean isDataFolder_1_b_3_Created = FolderList.dataFolder_1_b_3.mkdir();
                    }
                }
                break;
            }

            case "1.15": {
                if (!FolderList.versionFolder_2.exists()) {
                    boolean isVersionFolder_2_Created = FolderList.versionFolder_2.mkdir();
                }

                if (onlineMode) {
                    if (!FolderList.onlineFolder_2.exists()) {
                        boolean isOnlineFolder_2_Created = FolderList.onlineFolder_2.mkdir();
                    }

                    if (!FolderList.serverFolder_2_a.exists()) {
                        boolean isServerFolder_2_a_Created = FolderList.serverFolder_2_a.mkdir();
                    }

                    if (!FolderList.serverDataFolder_2_a.exists()) {
                        boolean isServerDataFolder_2_a_Created = FolderList.serverDataFolder_2_a.mkdir();
                    }

                    if (!FolderList.dataFolder_2_a_1.exists()) {
                        boolean isDataFolder_2_a_1_Created = FolderList.dataFolder_2_a_1.mkdir();
                    }

                    if (!FolderList.dataFolder_2_a_2.exists()) {
                        boolean isDataFolder_2_a_2_Created = FolderList.dataFolder_2_a_2.mkdir();
                    }

                    if (!FolderList.dataFolder_2_a_3.exists()) {
                        boolean isDataFolder_2_a_3_Created = FolderList.dataFolder_2_a_3.mkdir();
                    }
                } else {
                    if (!FolderList.offlineFolder_2.exists()) {
                        boolean isOfflineFolder_2_Created = FolderList.offlineFolder_2.mkdir();
                    }

                    if (!FolderList.serverFolder_2_b.exists()) {
                        boolean isServerFolder_2_b_Created = FolderList.serverFolder_2_b.mkdir();
                    }

                    if (!FolderList.serverDataFolder_2_b.exists()) {
                        boolean isServerDataFolder_2_b_Created = FolderList.serverDataFolder_2_b.mkdir();
                    }

                    if (!FolderList.dataFolder_2_b_1.exists()) {
                        boolean isDataFolder_2_b_1_Created = FolderList.dataFolder_2_b_1.mkdir();
                    }

                    if (!FolderList.dataFolder_2_b_2.exists()) {
                        boolean isDataFolder_2_b_2_Created = FolderList.dataFolder_2_b_2.mkdir();
                    }

                    if (!FolderList.dataFolder_2_b_3.exists()) {
                        boolean isDataFolder_2_b_3_Created = FolderList.dataFolder_2_b_3.mkdir();
                    }
                }
                break;
            }

            case "1.16": {
                if (!FolderList.versionFolder_3.exists()) {
                    boolean isVersionFolder_3_Created = FolderList.versionFolder_3.mkdir();
                }

                if (onlineMode) {
                    if (!FolderList.onlineFolder_3.exists()) {
                        boolean isOnlineFolder_3_Created = FolderList.onlineFolder_3.mkdir();
                    }

                    if (!FolderList.serverFolder_3_a.exists()) {
                        boolean isServerFolder_3_a_Created = FolderList.serverFolder_3_a.mkdir();
                    }

                    if (!FolderList.serverDataFolder_3_a.exists()) {
                        boolean isServerDataFolder_3_a_Created = FolderList.serverDataFolder_3_a.mkdir();
                    }

                    if (!FolderList.dataFolder_3_a_1.exists()) {
                        boolean isDataFolder_3_a_1_Created = FolderList.dataFolder_3_a_1.mkdir();
                    }

                    if (!FolderList.dataFolder_3_a_2.exists()) {
                        boolean isDataFolder_3_a_2_Created = FolderList.dataFolder_3_a_2.mkdir();
                    }

                    if (!FolderList.dataFolder_3_a_3.exists()) {
                        boolean isDataFolder_3_a_3_Created = FolderList.dataFolder_3_a_3.mkdir();
                    }
                } else {
                    if (!FolderList.offlineFolder_3.exists()) {
                        boolean isOfflineFolder_3_Created = FolderList.offlineFolder_3.mkdir();
                    }

                    if (!FolderList.serverFolder_3_b.exists()) {
                        boolean isServerFolder_3_b_Created = FolderList.serverFolder_3_b.mkdir();
                    }

                    if (!FolderList.serverDataFolder_3_b.exists()) {
                        boolean isServerDataFolder_3_b_Created = FolderList.serverDataFolder_3_b.mkdir();
                    }

                    if (!FolderList.dataFolder_3_b_1.exists()) {
                        boolean isDataFolder_3_b_1_Created = FolderList.dataFolder_3_b_1.mkdir();
                    }

                    if (!FolderList.dataFolder_3_b_2.exists()) {
                        boolean isDataFolder_3_b_2_Created = FolderList.dataFolder_3_b_2.mkdir();
                    }

                    if (!FolderList.dataFolder_3_b_3.exists()) {
                        boolean isDataFolder_3_b_3_Created = FolderList.dataFolder_3_b_3.mkdir();
                    }
                }
                break;
            }

            default: {
                break;
            }
        }
    }
}
