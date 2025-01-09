package com.islandstudio.neon

import com.islandstudio.neon.shared.core.AppContext
import com.islandstudio.neon.shared.core.di.AppDIManager
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.stable.core.application.di.module.DatabaseModule
import com.islandstudio.neon.stable.core.application.di.module.GeneralModule
import com.islandstudio.neon.stable.core.application.di.module.RepositoryModule
import com.islandstudio.neon.stable.core.application.init.AppLoader
import com.islandstudio.neon.stable.core.database.DatabaseInterface
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.inject
import org.koin.ksp.generated.module

class Neon : JavaPlugin(), IComponentInjector {
    private val appLoader by inject<AppLoader>()
    private val appContext by inject<AppContext>()
    private val databaseInterface by inject<DatabaseInterface>()
    private var isPreLoaded = false
    private var isPostLoaded = false

    init {
        AppDIManager.loadDIModule(
            GeneralModule().module,
            DatabaseModule().module,
            RepositoryModule().module
        ).run()
    }

    override fun onLoad() {
        appLoader.preLoad().apply { isPreLoaded = this }
    }

    override fun onEnable() {
        appLoader.postLoad().apply { isPostLoaded = this }

        if (!(isPreLoaded && isPostLoaded)) return

        server.consoleSender.sendMessage(AppLoader.NEON_ON_ENABLED_TITLE)
    }

    override fun onDisable() {
        if (!(isPreLoaded && isPostLoaded)) return

        databaseInterface.disconnect()

        server.consoleSender.sendMessage(AppLoader.NEON_ON_DISABLED_TITLE)
    }

    fun getPluginClassLoader(): ClassLoader = this.classLoader

    @JvmName("getNeonAppInitializer")
    fun getAppInitializer(): AppLoader = this.appLoader
}