package com.islandstudio.neon.stable.utils

import com.islandstudio.neon.Neon
import org.bukkit.NamespacedKey
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin.getPlugin

enum class NNamespaceKeys(val key: NamespacedKey, val plugin: Plugin = getPlugin(Neon::class.java)) {

    NEON_BUTTON(NamespacedKey(Handler.plugin, "neon_button")),
    NEON_BUTTON_HIGHLIGHT(NamespacedKey(Handler.plugin, "neon_button_highlight")),
    NEON_BUNDLE(NamespacedKey(Handler.plugin, "bundle"))
}

private object Handler {
    val plugin: Plugin = getPlugin(Neon::class.java)
}