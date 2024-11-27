package com.islandstudio.neon.stable.core.io.nFile

import com.islandstudio.neon.stable.core.application.AppContext
import com.islandstudio.neon.stable.core.application.di.ModuleInjector
import com.islandstudio.neon.stable.core.application.init.NConstructor
import java.io.File

sealed class NeonDataFolder(folder: File): File(folder.toPath().toString()) {
    companion object: ModuleInjector {
        private val appContext by getKoin().inject<AppContext>()
        private val serverRunningMode = appContext.serverRunningMode

        fun getAllDataFolder(): ArrayList<File> {
            return NeonDataFolder::class.sealedSubclasses
                .map {
                    it.objectInstance as File
                }.toCollection(ArrayList())
        }
    }

    data object ModeFolder: NeonDataFolder(
        File(NFile.getDataFolder(), "${NConstructor.getMajorVersion()}$separator${serverRunningMode.value}")
    ) {
        private fun readResolve(): Any = ModeFolder
    }


    data object NeonDatabaseFolder: NeonDataFolder(
        File(NFile.getDataFolder(), "database")
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
        File(NFile.getDataFolder(), "extensions")
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