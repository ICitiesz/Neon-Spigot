package com.islandstudio.neon.stable.core.io.nFile

import com.islandstudio.neon.stable.core.application.di.ModuleInjector
import com.islandstudio.neon.stable.core.application.init.AppInitializer
import com.islandstudio.neon.stable.core.application.init.NConstructor
import java.io.File

enum class FolderList(val folder: File) {
    /* All the required folders will be here */
    /* Stable */
    MODE_FOLDER(File(NFile.getDataFolder(), "${NConstructor.getMajorVersion()}${File.separator}${Handler.serverRunningMode.value}")),
    NEON_DATABASE_FOLDER(File(NFile.getDataFolder(), "database")),
    NSERVERFEATURES_FOLDER(File(MODE_FOLDER.folder, "nServerFeatures")),
    NEXPERIMENTAL_FOLDER(File(NSERVERFEATURES_FOLDER.folder, "nExperimental")),
    NPROFILE(File(MODE_FOLDER.folder, "nProfile")),
    NWAYPOINTS_FOLDER(File(NSERVERFEATURES_FOLDER.folder, "nWaypoints")),

    /* Experimental */
    NFIREWORKS(File(NEXPERIMENTAL_FOLDER.folder, "nFireworks")),
    NFIREWORKS_IMAGES(File(NFIREWORKS.folder, "images")),
    NFIREWORKS_PATTERN_FRAMES(File(NFIREWORKS.folder, "patterns")),
    NPAINTING(File(NEXPERIMENTAL_FOLDER.folder, "nPainting")),
    NPAINTING_IMAGES(File(NPAINTING.folder, "images")),
    NPAINTING_RENDER_DATA(File(NPAINTING.folder, "render_data"));


    private object Handler: ModuleInjector {
        val serverRunningMode = AppInitializer.serverRunningMode
    }
}