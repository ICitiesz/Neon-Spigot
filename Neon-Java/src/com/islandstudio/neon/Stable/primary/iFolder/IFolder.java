package com.islandstudio.neon.stable.primary.iFolder;


import com.islandstudio.neon.stable.primary.iConstructor.IConstructor;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class IFolder {
    public static class Handler {
        /**
         * Initialization for the folders
         */
        @SuppressWarnings("ResultOfMethodCallIgnored")
        public static void init() {
            Arrays.stream(FolderList.values()).forEach(folderList -> {
                if (!folderList.getFolder().exists()) folderList.getFolder().mkdirs();
            });
        }
    }

    /**
     * Create new file by given file object.
     *
     * @param file The file need to be created.
     * @param requiredFolder The folder that contain the file.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void createNewFile(File file, File requiredFolder) {
        if (!requiredFolder.exists()) requiredFolder.mkdirs();

        if (file.exists()) return;

        try {
            file.createNewFile();
        } catch (IOException err) {
            System.out.println("An error occurred while trying to create file '" + file.getName() + "'!");
        }
    }

    /**
     * Get the Minecraft version which is required for creating folder for each individual version.
     *
     * @return The Minecraft version.
     */
    public static String getVersion() {
        final String VERSION = (IConstructor.getPlugin().getServer().getBukkitVersion().
                split("-")[0]).split("\\.")[0] + "." + (IConstructor.getPlugin().getServer()
                .getBukkitVersion().split("-")[0]).split("\\.")[1];

        return VERSION.replace(".", "_");
    }
}
