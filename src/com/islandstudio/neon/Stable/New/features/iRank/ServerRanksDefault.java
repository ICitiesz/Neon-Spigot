package com.islandstudio.neon.Stable.New.features.iRank;

public enum ServerRanksDefault {
    OWNER("[" + "OWNER" + "] "),
    VIP_PLUS("[" + "VIP" + "] "),
    VIP("[" + "VIP" + "] "),
    MEMBER("[" + "MEMBER" + "] ");

    private final String prefix;

    ServerRanksDefault(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
