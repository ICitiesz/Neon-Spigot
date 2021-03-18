package com.islandstudio.neon.Stable.New.PluginFeatures.RankSystem;

public enum ServerRanksDefaultState {
    OWNER("[" + "OWNER" + "] "),
    VIP_PLUS("[" + "VIP" + "] "),
    VIP("[" + "VIP" + "] "),
    MEMBER("[" + "MEMBER" + "] ");

    private final String prefix;

    ServerRanksDefaultState(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
