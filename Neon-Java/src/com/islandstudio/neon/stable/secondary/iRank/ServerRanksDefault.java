package com.islandstudio.neon.stable.secondary.iRank;

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
