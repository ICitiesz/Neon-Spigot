package com.islandstudio.neon.stable.utils

import com.islandstudio.neon.Neon
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin.getPlugin

object NReflector {
    /**
     * Get the Minecraft namespace class
     *
     * @param className The class name. (String)
     * @return The Minecraft namespace class. (Class)
     */
    fun getNamespaceClass(className: String): Class<*> {
        return Class.forName("net.minecraft.$className")
    }

    /**
     * Get the Neon class
     *
     * @param className The class name. (String)
     * @return The Neon class. (Class)
     */
    fun getNClassName(className: String): Class<*> {
        return Class.forName("com.islandstudio.$className")
    }
}