package com.islandstudio.neon.Stable.New.iCommand;

public enum Commands {
    WAYPOINTS("waypoints"),
    RANK("rank"),
    DEBUG("debug"),
    EXPERIMENTAL("experimental"),
    GM("gm"),
    REGEN("regen"),
    SERVERCONFIG("serverconfig"),
    EFS("efs"),
    MOD("mod");

    private final String commandAlias;

    Commands(String commandAlias) {
        this.commandAlias = commandAlias;
    }

    public String getCommandAlias() {
        return commandAlias;
    }
}
