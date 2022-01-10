package com.islandstudio.neon.stable.primary.nFolder

import com.islandstudio.neon.Main
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin.getPlugin
import java.io.File

object NFolder {
    private val plugin: Plugin = getPlugin(Main::class.java)
    private val version: String = (plugin.server.bukkitVersion.split("-")[0]).split(".")[0] + "_" + (plugin.server.bukkitVersion.split("-")[0]).split(".")[1]

    private val dataFolder: File = plugin.dataFolder

    /* Initialization for the folders */
    fun run() {
        FolderList.values().forEach {
            if (!it.folder.exists()) {
                it.folder.mkdirs()
            }
        }
    }

    /* Get the directory of Neon which inside the plugin directory within the Spigot server directory. */
    fun getDataFolder(): File {
        return dataFolder
    }

    /* Get the Spigot version which is required for creating folder for each individual version. */
    fun getVersion(): String {
        return version
    }

    /* Get the mode, either Online Mode or Offline Mode */
    fun getMode(): String {
        return if (plugin.server.onlineMode) {
            "online"
        } else {
            "offline"
        }
    }

}