package com.islandstudio.neon.Deprecated.Command;

@Deprecated
public enum CommandAlias {
    CMD_1("/mod"),
    CMD_2("gm"),
    CMD_3("regen"),
    CMD_4("efs"),
    CMD_5("rank"),
    CMD_6("serverconfig"),
    CMD_7("waypoints");

    private final String commandAlias;

    CommandAlias(String commandAlias) {
        this.commandAlias = commandAlias;
    }

    public String getCommandAlias() {
        return commandAlias;
    }
}
