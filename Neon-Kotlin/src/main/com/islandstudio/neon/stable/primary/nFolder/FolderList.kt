package com.islandstudio.neon.stable.primary.nFolder

import com.islandstudio.neon.stable.utils.ServerHandler
import java.io.File

enum class FolderList(val folder: File) {
    /* All the required folders will be here */
    /* Stable */
    SERVER_CONFIG_FOLDER(File(NFolder.getDataFolder(), "${NFolder.getVersion()}/${ServerHandler.getMode()}/server_config")),
    SERVER_DATA_FOLDER(File(NFolder.getDataFolder(), "${NFolder.getVersion()}/${ServerHandler.getMode()}/server_data")),
    PLAYERS_FOLDER(File(SERVER_DATA_FOLDER.folder, "players")),
    NWAYPOINTS_FOLDER(File(SERVER_DATA_FOLDER.folder, "nWaypoints")),
    NEXPERIMENTAL_FOLDER(File(SERVER_DATA_FOLDER.folder, "nExperimental")),

    /* Experimental */
    SERVER_CONFIGURATION_FOLDER_NEW(File(NEXPERIMENTAL_FOLDER.folder, "nServerConfiguration"));
}