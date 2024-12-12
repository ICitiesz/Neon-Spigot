package com.islandstudio.neon.stable.core.io.nFile

import com.islandstudio.neon.Neon
import com.islandstudio.neon.stable.core.application.di.IComponentInjector
import org.koin.core.component.inject
import java.io.File

@Deprecated("Merged into NeonDataFolder")
object NFile: IComponentInjector {
    private val neon by inject<Neon>()

    /**
     * Initialization for the folders
     */
    fun run() {
        reformatVersionFolder()

        NeonDataFolder.getAllDataFolder().forEach {
            if (it.exists()) return@forEach

            it.mkdirs()
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
    //TODO: Need to change file object to use string instead
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
     * @return The data folder of Neon. [File]
     */
    fun getDataFolder(): File {
        return neon.dataFolder.apply {
            if (!this.exists()) this.mkdirs()
        }
    }

    /**
     * Reformat version folder from older formart, '1_17' to new format '1.17'
     *
     */
    private fun reformatVersionFolder() {
        /* Old version format: '1_17' */
        getDataFolder().listFiles()?.let {
            it.filter { folder -> folder.isDirectory && folder.name.matches("^\\d_\\d\\d\$".toRegex()) }
                .forEach { folder ->
                    folder.renameTo(File(getDataFolder(), folder.name.replace("_", ".")))
            }
        }
    }
}