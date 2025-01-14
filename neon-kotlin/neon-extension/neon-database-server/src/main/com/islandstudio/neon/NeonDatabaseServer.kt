package com.islandstudio.neon

import com.islandstudio.neon.application.di.module.GeneralModule
import com.islandstudio.neon.event.ServerConstantEvent
import com.islandstudio.neon.shared.core.AppContext
import com.islandstudio.neon.shared.core.di.AppDIManager
import com.islandstudio.neon.shared.core.di.IComponentInjector
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.inject
import org.koin.ksp.generated.module

class NeonDatabaseServer: JavaPlugin(), IComponentInjector {
    init {
        AppDIManager.loadDIModule(
            GeneralModule().module
        ).run()
    }

    override fun onLoad() {
        val appContext by inject<AppContext>()
        val databaseController by inject<DatabaseController>()

        appContext.loadCodeMessages()
        appContext.ensureVersionCompatible()
        appContext.ensureParentPluginLoaded()

        databaseController.initDatabaseServer()
    }

    override fun onEnable() {
        ServerConstantEvent.registerEvent()
    }

    override fun onDisable() {
        ServerConstantEvent.unregisterEvent()
    }
}