package com.islandstudio.neondatabaseserver

import com.islandstudio.neondatabaseserver.application.AppContext
import com.islandstudio.neondatabaseserver.application.di.AppModuleInjection
import com.islandstudio.neondatabaseserver.application.di.ModuleInjector
import com.islandstudio.neondatabaseserver.event.ServerConstantEvent
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.inject

class NeonDatabaseServer: JavaPlugin(), ModuleInjector {
    init {
        AppModuleInjection.run()
    }

    override fun onLoad() {
        val appContext by inject<AppContext>()

        appContext.loadCodeMessages()

        if (!validateParentPlugin()) {
            return this@NeonDatabaseServer.server.logger.warning(appContext.getCodeMessage("neon_database_server.warning.neon_not_running"))
        }

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