package com.islandstudio.neon.stable.primary.nFolder

import com.islandstudio.neon.stable.utils.ServerHandler
import java.io.File

enum class FolderList(val folder: File) {
    /* All the required folders will be here */
    /* Stable */
    MODE_FOLDER(File(NFolder.getDataFolder(), "${NFolder.getVersion()}/${ServerHandler.getMode()}")),
    SERVER_CONFIG_FOLDER(File(MODE_FOLDER.folder, "nServerFeatures")),
    NEXPERIMENTAL_FOLDER(File(MODE_FOLDER.folder, "nExperimental")),
    NPROFILE(File(MODE_FOLDER.folder, "nProfile")),
    NWAYPOINTS_FOLDER(File(SERVER_CONFIG_FOLDER.folder, "nWaypoints")),

    /* Experimental */
    NSERVERFEATURES_BETA_FOLDER(File(NEXPERIMENTAL_FOLDER.folder, "nServerFeaturesBeta"));
}