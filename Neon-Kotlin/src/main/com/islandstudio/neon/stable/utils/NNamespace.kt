package com.islandstudio.neon.stable.utils

import com.islandstudio.neon.Main
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin.getPlugin

object NNamespace {
    private val plugin: Plugin = getPlugin(Main::class.java)

    fun getNamespaceClass(className: String): Class<*> {
        return Class.forName("net.minecraft.$className")
    }
}