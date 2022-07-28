package com.islandstudio.neon.stable.utils

import com.islandstudio.neon.Neon
import com.islandstudio.neon.stable.primary.nConstructor.NConstructor
import org.bukkit.NamespacedKey
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin.getPlugin

enum class NNamespaceKeys(val key: NamespacedKey) {

    NEON_BUTTON(NamespacedKey(NConstructor.plugin, "neon_button")),
    NEON_BUTTON_HIGHLIGHT(NamespacedKey(NConstructor.plugin, "neon_button_highlight")),
    NEON_BUNDLE(NamespacedKey(NConstructor.plugin, "neon_bundle")),
    NEON_DAMAGED_ITEM(NamespacedKey(NConstructor.plugin, "neon_damaged_item"))
}