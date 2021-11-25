package com.islandstudio.neon.Experimental;

public enum GameModes {
    SURVIVAL_MODE(0),
    CREATIVE_MODE(1),
    ADVENTURE_MODE(2),
    SPECTATOR_MODE(3);

    private final int value;

    GameModes(int value) {
        this.value = value;
    }

    public int getGameModeValue() {
        return value;
    }
}