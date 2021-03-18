package com.islandstudio.neon.Stable.New.Initialization.FolderManager;

import java.io.File;

public enum FolderList {

    FOLDER_A(new File(FolderHandler.getDataFolder(), FolderHandler.getVersion() + "\\" + FolderHandler.getMode() + "\\Server_Configuration")),
    FOLDER_B(new File(FolderHandler.getDataFolder(), FolderHandler.getVersion() + "\\" + FolderHandler.getMode() + "\\Server_Data\\Player_Data")),
    FOLDER_C(new File(FolderHandler.getDataFolder(), FolderHandler.getVersion() + "\\" + FolderHandler.getMode() + "\\Server_Data\\Chat_Logs")),
    FOLDER_D(new File(FolderHandler.getDataFolder(), FolderHandler.getVersion() + "\\" + FolderHandler.getMode() + "\\Server_Data\\iWaypoints"));

    private final File folder;

    FolderList(File folder) {
        this.folder = folder;
    }

    public File getFolder() {
        return folder;
    }
}
