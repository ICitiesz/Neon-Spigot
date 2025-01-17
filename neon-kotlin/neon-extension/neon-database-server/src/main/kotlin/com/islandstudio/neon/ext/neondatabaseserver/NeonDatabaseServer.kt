package com.islandstudio.neon.ext.neondatabaseserver

import com.islandstudio.neon.api.IAPIAdapter
import com.islandstudio.neon.ext.neondatabaseserver.core.DatabaseServerManager
import com.islandstudio.neon.ext.neondatabaseserver.core.application.di.module.NeonDatabaseServerModule
import com.islandstudio.neon.ext.neondatabaseserver.event.ServerConstantEvent
import com.islandstudio.neon.shared.core.AppContext
import com.islandstudio.neon.shared.core.di.AppDIManager
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.di.module.DataSourceModule
import com.islandstudio.neon.shared.core.di.module.NeonAPIModule
import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.ksp.generated.module
import java.util.logging.Filter
import java.util.logging.LogRecord

class NeonDatabaseServer: JavaPlugin(), IComponentInjector {
    val databaseServerManager by inject<DatabaseServerManager>()

    init {
        AppDIManager.loadDIModule(
            NeonDatabaseServerModule().module,
            DataSourceModule().module,
            NeonAPIModule().module
        ).run()
    }

    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    override fun onLoad() {
        val appContext by inject<AppContext>()

        newSingleThreadContext("Neon Database Server").use { dispatcher ->
            CoroutineScope(dispatcher).async {
                appContext.loadCodeMessages()

                this@NeonDatabaseServer.logger.info(appContext.getCodeMessage("neon.info.appcontext.parent_plugin_check"))
                appContext.ensureParentPluginLoaded()

                delay(100)

                this@NeonDatabaseServer.logger.info(appContext.getCodeMessage("neon.info.apploader.version_check"))
                appContext.ensureVersionCompatible(
                    appContext.getFormattedCodeMessage(
                        "neon.error.apploader.extension_load_incompatible_version",
                        this@NeonDatabaseServer.name
                    )
                )
                delay(100)

                this@NeonDatabaseServer.server.logger.filter = object : Filter {
                    override fun isLoggable(record: LogRecord?): Boolean {
                        record?.let {
                            return !it.message.contains("Nag author(s): '[ICities]' of 'Neon-Database-Server v1.0' about their usage of System.out/err.print.")
                        }

                        return false
                    }
                }

                databaseServerManager.initializeDatabaseServer()
            }.asCompletableFuture().join()
        }
    }

    override fun onEnable() {
        ServerConstantEvent.Companion.registerEvent()
    }

    override fun onDisable() {
        ServerConstantEvent.Companion.unregisterEvent()
    }

    fun getNeonAPIAdapter(): IAPIAdapter {
        return get<IAPIAdapter>()
    }
}