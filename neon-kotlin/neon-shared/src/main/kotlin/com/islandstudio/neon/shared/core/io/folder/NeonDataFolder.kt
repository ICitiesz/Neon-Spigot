package com.islandstudio.neon.shared.core.io.folder

import com.islandstudio.neon.shared.core.AppContext
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.io.resource.NeonExternalResource
import org.koin.core.component.inject
import java.io.File

sealed class NeonDataFolder(folder: File): File(folder.toPath().toString()) {
    companion object: IComponentInjector {
        private val appContext by inject<AppContext>()
        private val parentPlugin = appContext.getParentPlugin()

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
            return NeonDataFolder::class.sealedSubclasses
                .map {
                    it.objectInstance as File
                }.toCollection(ArrayList())
        }

        /**
         * Get the root data folder of Neon which inside the plugin directory within the server directory.
         * @return The root data folder of Neon. [File]
         */
        fun getRootDataFolder(): File {
            return parentPlugin.dataFolder.apply {
                if (!this.exists()) this.mkdirs()
            }
        }

        /**
         * Reformat version folder from older formart, '1_17' to new format '1.17'
         */
        fun reformatVersionFolder() {
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

    data object VersionFolder: NeonDataFolder(
        File(getRootDataFolder(), appContext.serverMajorVersion)
    ) {
        private fun readResolve(): Any = VersionFolder
    }

    data object ModeFolder: NeonDataFolder(
        File(VersionFolder, appContext.serverRunningMode.value)
    ) {
        private fun readResolve(): Any = ModeFolder
    }


    data object NeonDatabaseFolder: NeonDataFolder(
        File(getRootDataFolder(), "database")
    ) {
        private fun readResolve(): Any = NeonDatabaseFolder
    }

    data object NServerFeaturesFolder: NeonDataFolder(
        File(ModeFolder, "nServerFeatures")
    ) {
        private fun readResolve(): Any = NServerFeaturesFolder
    }

    data object NExperimentalFolder: NeonDataFolder(
        File(NServerFeaturesFolder, "nExperimental")
    ) {
        private fun readResolve(): Any = NExperimentalFolder
    }

    data object NProfileFolder: NeonDataFolder(
        File(ModeFolder, "nProfile")
    ) {
        private fun readResolve(): Any = NProfileFolder
    }

    data object NWaypointsFolder: NeonDataFolder(
        File(NServerFeaturesFolder, "nWaypoints")
    ) {
        private fun readResolve(): Any = NWaypointsFolder
    }

    data object ExtensionFolder: NeonDataFolder(
        File(getRootDataFolder(), "extensions")
    ) {
        private fun readResolve(): Any = ExtensionFolder
    }

    /* Experimental */
    data object NFireworksFolder: NeonDataFolder(
        File(NExperimentalFolder, "nFireworks")
    ) {
        private fun readResolve(): Any = NFireworksFolder
    }

    data object NFireworkdsImageFolder: NeonDataFolder(
        File(NFireworksFolder, "images")
    ) {
        private fun readResolve(): Any = NFireworkdsImageFolder
    }

    data object NFireworksPatternFramesFolder: NeonDataFolder(
        File(NFireworksFolder, "patterns")
    ) {
        private fun readResolve(): Any = NFireworksPatternFramesFolder
    }

    data object NPaintingFolder: NeonDataFolder(
        File(NExperimentalFolder, "nPainting")
    ) {
        private fun readResolve(): Any = NPaintingFolder
    }

    data object NPaintingImageFolder: NeonDataFolder(
        File(NPaintingFolder, "images")
    ) {
        private fun readResolve(): Any = NPaintingImageFolder
    }

    data object NPaintingRenderDataFolder: NeonDataFolder(
        File(NPaintingFolder, "render_data")
    ) {
        private fun readResolve(): Any = NPaintingRenderDataFolder
    }
}