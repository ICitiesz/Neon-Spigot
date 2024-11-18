package com.islandstudio.neon

import com.islandstudio.neon.stable.core.application.AppContext
import com.islandstudio.neon.stable.core.application.di.AppModuleInjection
import com.islandstudio.neon.stable.core.application.di.ModuleInjector
import com.islandstudio.neon.stable.core.application.init.AppInitializer
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.inject

class Neon : JavaPlugin(), ModuleInjector {
    private lateinit var appInitializer: AppInitializer

    override fun onLoad() {
        AppModuleInjection.run().apply {
            val appContext by inject<AppContext>()

            appContext.loadCodeMessages()

            appInitializer = AppInitializer()
        }

        appInitializer.preInit()
    }

    override fun onEnable() {
        appInitializer.postInit()
        server.consoleSender.sendMessage(AppInitializer.NEON_ON_ENABLED_TITLE)
    }

    override fun onDisable() {
        server.consoleSender.sendMessage(AppInitializer.NEON_ON_DISABLED_TITLE)
    }

    fun getPluginClassLoader(): ClassLoader = this.classLoader

    fun getAppInitializer(): AppInitializer = this.appInitializer
}