package com.islandstudio.neon.stable.primary.nFolder

import java.io.File

enum class FolderList(val folder: File) {
    /* All the required folders will be here */
    FOLDER_A(File(NFolder.getDataFolder(), NFolder.getVersion() + "/" + NFolder.getMode() + "/server_config")),
    FOLDER_B(File(NFolder.getDataFolder(), NFolder.getVersion() + "/" + NFolder.getMode() + "/server_data/players")),
    FOLDER_C(File(NFolder.getDataFolder(), NFolder.getVersion() + "/" + NFolder.getMode() + "/server_data/nWaypoints")),
    FOLDER_D(File(NFolder.getDataFolder(), NFolder.getVersion() + "/" + NFolder.getMode() + "/server_data/nExperimental")),
}