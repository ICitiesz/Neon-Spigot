package com.islandstudio.neon.stable.core.io.nFolder

import com.islandstudio.neon.Neon
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin.getPlugin
import java.io.File

object NFolder {
    private val plugin: Plugin = getPlugin(Neon::class.java)
    private val version: String = (plugin.server.bukkitVersion.split("-")[0]).split(".")[0] + "_" + (plugin.server.bukkitVersion.split("-")[0]).split(".")[1]

    private val dataFolder: File = plugin.dataFolder

    /**
     * Initialization for the folders
     */
    fun run() {
        FolderList.entries.forEach {
            if (!it.folder.exists()) {
                it.folder.mkdirs()
            }
        }
    }

    /**
     * Create new file.
     *
     * @param requiredFolder The folder that contain the file.
     * @param file The file need to create.
     */
    fun createNewFile(requiredFolder: File, file: File) {
        if (!requiredFolder.exists()) requiredFolder.mkdirs()

        if (file.exists()) return

        file.createNewFile()
    }

    /**
     * Create new file if not exist, then get the created file, else get the existing file.
     *
     * @param requiredFolder The folder that contain the file.
     * @param file The target file.
     * @return The created file or existing file.
     */
    fun createOrGetNewFile(requiredFolder: File, file: File): File {
        if (!requiredFolder.exists()) requiredFolder.mkdirs()

        if (file.exists()) return file

        file.createNewFile()

        return file
    }

    fun createOrGetNewDirectory(directory: File): File {
        if (directory.exists()) return directory

        directory.mkdirs()

        return directory
    }

    /**
     * Get the data folder of Neon which inside the plugin directory within the Spigot server directory.
     * @return The data folder of Neon. (File)
     */
    fun getDataFolder(): File {
        return dataFolder
    }

    /**
     * Get the Minecraft version which is required for creating folder for each individual version.
     *
     * @return The Minecraft version. (String)
     */
    fun getVersion(): String {
        return version
    }

}