package com.islandstudio.neon

import com.islandstudio.neon.stable.core.application.AppContext
import com.islandstudio.neon.stable.core.application.di.AppModuleInjection
import com.islandstudio.neon.stable.core.application.init.AppInitializer
import com.islandstudio.neon.stable.core.application.reflection.NmsProcessor
import org.bukkit.plugin.java.JavaPlugin

class Neon : JavaPlugin() {
    private lateinit var appInitializer: AppInitializer

    override fun onLoad() {
        AppModuleInjection.run().apply {
            appInitializer = AppInitializer()
            AppContext.loadCodeMessages()
        }

        appInitializer.preInit()
    }

    override fun onEnable() {
        appInitializer.postInit()
        server.consoleSender.sendMessage(AppInitializer.NEON_ON_ENABLED_TITLE)
        NmsProcessor.run()
    }

    override fun onDisable() {
        server.consoleSender.sendMessage(AppInitializer.NEON_ON_DISABLED_TITLE)
    }

    fun getPluginClassLoader(): ClassLoader = this.classLoader

    fun getAppInitializer(): AppInitializer = this.appInitializer
}