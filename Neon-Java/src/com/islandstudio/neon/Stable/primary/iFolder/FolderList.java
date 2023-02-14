package com.islandstudio.neon.stable.primary.iFolder;

import com.islandstudio.neon.stable.primary.iConstructor.IConstructor;
import com.islandstudio.neon.stable.secondary.iServerConstantProcessor.IServerConstantProcessor;

import java.io.File;

public enum FolderList {
    /* All the required folders will be here */
    DATA_FOLDER(IConstructor.getPlugin().getDataFolder()),
    MODE_FOLDER(new File(FolderList.DATA_FOLDER.folder, IFolder.getVersion() + "/" + IServerConstantProcessor.getServerMode())),

    SERVER_CONFIGURATION(new File(FolderList.MODE_FOLDER.folder, "/Server_Configuration")),
    PLAYER_DATA(new File(FolderList.MODE_FOLDER.folder, "/Server_Data/Player_Data")),
    IWAYPOINTS(new File(FolderList.MODE_FOLDER.folder, "/Server_Data/iWaypoints")),
    IEXPERIMENTAL(new File(FolderList.MODE_FOLDER.folder, "/Server_Data/iExperimental"));

    private final File folder;

    FolderList(File folder) {
        this.folder = folder;
    }

    public File getFolder() {
        return folder;
    }
}
