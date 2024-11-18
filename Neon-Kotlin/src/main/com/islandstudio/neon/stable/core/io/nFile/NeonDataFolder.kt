package com.islandstudio.neon.stable.core.io.nFile

import com.islandstudio.neon.stable.core.application.AppContext
import com.islandstudio.neon.stable.core.application.di.ModuleInjector
import com.islandstudio.neon.stable.core.application.init.NConstructor
import org.koin.core.component.inject
import java.io.File
import kotlin.reflect.full.createInstance

sealed class NeonDataFolder(folder: File): File(folder.toPath().toString()) {
    companion object: ModuleInjector {
        private val appContext by inject<AppContext>()
        private val serverRunningMode = appContext.serverRunningMode

        fun getAllDataFolder(): ArrayList<File> {
            return NeonDataFolder::class.sealedSubclasses
                .map {
                    it.createInstance()
                }.toCollection(ArrayList())
        }
    }

    data class ModeFolder(
        val folder: File = File(
            NFile.getDataFolder(),
            "${NConstructor.getMajorVersion()}$separator${serverRunningMode.value}"
        )
    ): NeonDataFolder(folder)

    data class NeonDatabaseFolder(
        val folder: File = File(
            NFile.getDataFolder(),
            "database"
        )
    ): NeonDataFolder(folder)

    data class NServerFeaturesFolder(
        val folder: File = File(
            ModeFolder(),
            "nServerFeatures"
        )
    ): NeonDataFolder(folder)

    data class NExperimentalFolder(
        val folder: File = File(
            NServerFeaturesFolder(),
            "nExperimental"
        )
    ): NeonDataFolder(folder)

    data class NProfileFolder(
        val folder: File = File(
            ModeFolder(),
            "nProfile"
        )
    ): NeonDataFolder(folder)

    data class NWaypointsFolder(
        val folder: File = File(
            NServerFeaturesFolder(),
            "nWaypoints"
        )
    ): NeonDataFolder(folder)

    data class ExtensionFolder(
        val folder: File  = File(
            NFile.getDataFolder(),
            "extensions"
        )
    ): NeonDataFolder(folder)

    /* Experimental */
    data class NFireworksFolder(
        val folder: File = File(
            NExperimentalFolder(),
            "nFireworks"
        )
    ): NeonDataFolder(folder)

    data class NFireworkdsImageFolder(
        val folder: File = File(
            NFireworksFolder(),
            "images"
        )
    ): NeonDataFolder(folder)

    data class NFireworksPatternFramesFolder(
        val folder: File = File(
            NFireworksFolder(),
            "patterns"
        )
    ): NeonDataFolder(folder)

    data class NPaintingFolder(
        val folder: File = File(
            NExperimentalFolder(),
            "nPainting"
        )
    ): NeonDataFolder(folder)

    data class NPaintingImageFolder(
        val folder: File = File(
            NPaintingFolder(),
            "images"
        )
    ): NeonDataFolder(folder)

    data class NPaintingRenderDataFolder(
        val folder: File = File(
            NPaintingFolder(),
            "render_data"
        )
    ): NeonDataFolder(folder)
}