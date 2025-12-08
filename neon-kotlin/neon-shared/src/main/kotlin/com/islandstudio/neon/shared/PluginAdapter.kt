package com.islandstudio.neon.shared

import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

//@Single
class PluginAdapter<T: JavaPlugin>(val plugin: T) {
    fun getPluginClassLoader(): ClassLoader {
        return plugin.javaClass.classLoader
    }

    fun getPluginLogger(): Logger {
        return plugin.logger
    }
}