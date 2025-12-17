package com.islandstudio.neon.shared.experimental

import com.islandstudio.neon.shared.core.di.IComponentProvider
import com.islandstudio.neon.shared.core.di.getComponent
import com.islandstudio.neon.shared.core.initialization.IPluginContext
import com.islandstudio.neon.shared.core.io.resource.NeonExternalResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

sealed class NeonDataFolderNew(folder: File): File(folder.toPath().toString()) {
    companion object: IComponentProvider {
        private val pluginContext = getComponent<IPluginContext>()

        /**
         * Create and get the new file with NeonExternalResources
         *
         * @param neonExternalResource Defined NeonExternalResources
         * @return
         */
        fun createNewFile(neonExternalResource: NeonExternalResource): File {
            return createNewFile(neonExternalResource.neonDataFolder, neonExternalResource.resourceName)
        }

        /**
         * Create and get the new file.
         *
         * @param requiredFolder The required folder that contains the new file.
         * @param fileName The new file name.
         * @return
         */
        fun createNewFile(requiredFolder: File, fileName: String): File {
            if (!requiredFolder.exists()) requiredFolder.mkdirs()

            val newFile = File(requiredFolder, fileName)

            if (!newFile.exists()) newFile.createNewFile()

            return newFile
        }

        /**
         * Get all Neon data folder
         *
         * @return
         */
        fun getAllDataFolder(): ArrayList<File> {
            return NeonDataFolderNew::class.sealedSubclasses
                .map {
                    it.objectInstance as File
                }.toCollection(ArrayList())
        }

        /**
         * Get the root data folder of Neon which inside the plugin directory within the server directory.
         * @return The root data folder of Neon. [File]
         */
        fun getRootDataFolder(): File {
            return pluginContext.getParentPlugin().dataFolder.apply {
                if (!this.exists()) this.mkdirs()
            }
        }

        /**
         * Reformat version folder from older formart, '1_17' to new format '1.17'
         */
        suspend fun reformatVersionFolder() {
            withContext(Dispatchers.IO) {
                /* Old version format: '1_17'
                * New version format: '1.17' */
                getRootDataFolder().listFiles()?.let { folders ->
                    folders.filter { folder ->
                        folder.isDirectory && folder.name.matches("^\\d_\\d\\d\$".toRegex())
                    }.forEach { folder ->
                        folder.renameTo(File(getRootDataFolder(), folder.name.replace("_", ".")))
                    }
                }
            }
        }

    }

    data object VersionFolder: NeonDataFolderNew(
        File(getRootDataFolder(), pluginContext.serverMajorVersion)
    ) {
        private fun readResolve(): Any = VersionFolder
    }

    data object ModeFolder: NeonDataFolderNew(
        File(VersionFolder, pluginContext.serverOperationMode.value)
    ) {
        private fun readResolve(): Any = ModeFolder
    }

    data object NeonLibraryFolder: NeonDataFolderNew(
        File(getRootDataFolder(), "libs")
    ) {
        private fun readResolve(): Any = NeonLibraryFolder
    }

    data object NServerFeaturesFolder: NeonDataFolderNew(
        File(ModeFolder, "nServerFeatures")
    ) {
        private fun readResolve(): Any = NServerFeaturesFolder
    }

    data object NeonFeatureFolder: NeonDataFolderNew(
        File(ModeFolder, "NeonFeature")
    ) {
        private fun readResolve(): Any = NeonFeatureFolder
    }

    data object NExperimentalFolder: NeonDataFolderNew(
        File(NServerFeaturesFolder, "nExperimental")
    ) {
        private fun readResolve(): Any = NExperimentalFolder
    }

    data object NProfileFolder: NeonDataFolderNew(
        File(ModeFolder, "nProfile")
    ) {
        private fun readResolve(): Any = NProfileFolder
    }

    data object NWaypointsFolder: NeonDataFolderNew(
        File(NServerFeaturesFolder, "nWaypoints")
    ) {
        private fun readResolve(): Any = NWaypointsFolder
    }

    data object ExtensionFolder: NeonDataFolderNew(
        File(getRootDataFolder(), "extensions")
    ) {
        private fun readResolve(): Any = ExtensionFolder
    }

    data object NeonDatabaseFolder: NeonDataFolderNew(
        File(getRootDataFolder(), "database")
    ) {
        private fun readResolve(): Any = NeonDatabaseFolder
    }

    data object NeonDatabaseFolderNew: NeonDataFolderNew(
        File(getRootDataFolder(), "database-new")
    ) {
        private fun readResolve(): Any = NeonDatabaseFolderNew
    }

    data object NeonDatabaseDataFolder: NeonDataFolderNew(
        File(NeonDatabaseFolderNew, "data")
    ) {
        private fun readResolve(): Any = NeonDatabaseDataFolder
    }

    data object NeonDatabaseCoreFolder: NeonDataFolderNew(
        File(NeonDatabaseFolderNew, "core")
    ) {
        private fun readResolve(): Any = NeonDatabaseCoreFolder
    }

    /* Experimental */
    data object NFireworksFolder: NeonDataFolderNew(
        File(NExperimentalFolder, "nFireworks")
    ) {
        private fun readResolve(): Any = NFireworksFolder
    }

    data object NFireworkdsImageFolder: NeonDataFolderNew(
        File(NFireworksFolder, "images")
    ) {
        private fun readResolve(): Any = NFireworkdsImageFolder
    }

    data object NFireworksPatternFramesFolder: NeonDataFolderNew(
        File(NFireworksFolder, "patterns")
    ) {
        private fun readResolve(): Any = NFireworksPatternFramesFolder
    }

    data object NPaintingFolder: NeonDataFolderNew(
        File(NExperimentalFolder, "nPainting")
    ) {
        private fun readResolve(): Any = NPaintingFolder
    }

    data object NPaintingImageFolder: NeonDataFolderNew(
        File(NPaintingFolder, "images")
    ) {
        private fun readResolve(): Any = NPaintingImageFolder
    }

    data object NPaintingRenderDataFolder: NeonDataFolderNew(
        File(NPaintingFolder, "render_data")
    ) {
        private fun readResolve(): Any = NPaintingRenderDataFolder
    }
}