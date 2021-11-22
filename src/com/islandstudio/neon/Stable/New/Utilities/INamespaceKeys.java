package com.islandstudio.neon.Stable.New.Utilities;

import com.islandstudio.neon.MainCore;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public enum INamespaceKeys {
    NEON_BUTTON(new NamespacedKey(getPlugin(), "neon_button")),
    NEON_BUTTON_GLOW(new NamespacedKey(getPlugin(), "neon_button_glow")),
    NEON_EASTER_EGG(new NamespacedKey(getPlugin(), "neon_easter_egg"));

    private final NamespacedKey key;

    INamespaceKeys(NamespacedKey key) {
        this.key = key;
    }

    public NamespacedKey getKey() {
        return key;
    }

    private static Plugin getPlugin() {
        return MainCore.getPlugin(MainCore.class);
    }
}
