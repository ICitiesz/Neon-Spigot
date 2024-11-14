package com.islandstudio.neon

import com.islandstudio.neon.application.AppContext
import com.islandstudio.neon.event.ServerConstantEvent
import org.bukkit.plugin.java.JavaPlugin

class NeonDatabaseExtension: JavaPlugin() {
    override fun onLoad() {
        if (!validateParentPlugin()) return this.server.logger.warning(AppContext.getCodeMessages("neon_database.warning.neon_not_running"))

        AppContext.loadModuleInjection()
        AppContext.loadCodeMessages()
        DatabaseController.initDatabaseServer()
    }

    override fun onEnable() {

        ServerConstantEvent.registerEvent()
    }

    override fun onDisable() {
        ServerConstantEvent.unregisterEvent()
    }

    fun getPluginClassLoader(): ClassLoader = this.classLoader

    private fun validateParentPlugin(): Boolean {
        return runCatching {
            Class.forName("com.islandstudio.neon.Neon")
        }.mapCatching {
            true
        }.getOrElse {
            false
        }

        //return this.server.pluginManager.isPluginEnabled(getPlugin(Neon::class.java))
    }
}