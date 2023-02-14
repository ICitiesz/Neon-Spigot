package com.islandstudio.neon.stable.utils;

import com.islandstudio.neon.stable.primary.iConstructor.IConstructor;
import org.bukkit.NamespacedKey;

public enum INamespaceKeys {
    NEON_BUTTON(new NamespacedKey(IConstructor.getPlugin(), "neon_button")),
    NEON_BUTTON_HIGHLIGHTER(new NamespacedKey(IConstructor.getPlugin(), "neon_button_highlighter")),
    NEON_EASTER_EGG(new NamespacedKey(IConstructor.getPlugin(), "neon_easter_egg"));

    private final NamespacedKey key;

    INamespaceKeys(NamespacedKey key) {
        this.key = key;
    }

    public NamespacedKey getKey() {
        return key;
    }
}
