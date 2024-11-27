package com.islandstudio.neon

import com.islandstudio.neon.stable.core.application.AppContext
import com.islandstudio.neon.stable.core.application.di.AppModuleInjection
import com.islandstudio.neon.stable.core.application.di.ModuleInjector
import com.islandstudio.neon.stable.core.application.init.AppInitializer
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.inject

class Neon : JavaPlugin(), ModuleInjector {
    private val appInitializer by lazy { AppInitializer() }
    private val appContext by inject<AppContext>()

    init {
        AppModuleInjection.run()
    }

    override fun onLoad() {
        appContext.loadCodeMessages()
        appInitializer.preInit()
    }

    override fun onEnable() {
        appInitializer.postInit()

        if (!appContext.isVersionCompatible) return

        server.consoleSender.sendMessage(AppInitializer.NEON_ON_ENABLED_TITLE)
    }

    override fun onDisable() {
        if (!appContext.isVersionCompatible) return

        server.consoleSender.sendMessage(AppInitializer.NEON_ON_DISABLED_TITLE)
    }

    fun getPluginClassLoader(): ClassLoader = this.classLoader

    @JvmName("getNeonAppInitializer")
    fun getAppInitializer(): AppInitializer = this.appInitializer
}