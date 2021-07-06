package com.islandstudio.neon.Experimental.Commands;

public enum CMDAlias {
    WAYPOINTS("waypoints"),
    RANK("rank");

    private final String commandAlias;


    CMDAlias(String commandAlias) {
        this.commandAlias = commandAlias;
    }

    public String getCommandAlias() {
        return commandAlias;
    }
}
