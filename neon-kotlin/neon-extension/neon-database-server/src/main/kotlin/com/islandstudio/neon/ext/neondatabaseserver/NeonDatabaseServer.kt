package com.islandstudio.neon.ext.neondatabaseserver

import com.islandstudio.neon.ext.neondatabaseserver.core.ConnectionManager
import com.islandstudio.neon.ext.neondatabaseserver.core.DatabaseServerManager
import com.islandstudio.neon.ext.neondatabaseserver.core.application.di.module.NeonDatabaseServerModule
import com.islandstudio.neon.shared.core.AppContext
import com.islandstudio.neon.shared.core.di.AppDIManager
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.di.SharedModule
import com.islandstudio.neon.shared.core.initialization.IEventRegistry
import com.islandstudio.neon.shared.server.ServerManager
import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.server.ServerCommandEvent
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.inject
import org.koin.ksp.generated.module
import java.util.logging.Filter
import java.util.logging.LogRecord

class NeonDatabaseServer: JavaPlugin(), IComponentInjector, IEventRegistry {
    private val databaseServerManager by inject<DatabaseServerManager>()
    private val neonDatabaseServerEvent by lazy { NeonDatabaseServerEvent() }

    init {
        AppDIManager.loadDIModule(
            NeonDatabaseServerModule().module,
            SharedModule().module
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
        registerEvent(neonDatabaseServerEvent)
    }

    override fun onDisable() {
        unregisterEvent(neonDatabaseServerEvent)
    }

    fun getConnectionManager(): ConnectionManager {
        val connectionManager by inject<ConnectionManager>()

        return connectionManager
    }

    private class NeonDatabaseServerEvent: Listener, IComponentInjector {
        val databaseServerManager by inject<DatabaseServerManager>()

        @EventHandler
        private fun onServerCommandSend(e: ServerCommandEvent) {
            ServerManager.onServerReload(e) {
                databaseServerManager.stopDatabaseServer()
            }

            ServerManager.onServerClose(e) {
                databaseServerManager.stopDatabaseServer()
            }
        }

        @EventHandler
        private fun onPlayerCommandPreprocess(e: PlayerCommandPreprocessEvent) {
            ServerManager.onServerReload(e) {
                databaseServerManager.stopDatabaseServer()
            }

            ServerManager.onServerClose(e) {
                databaseServerManager.stopDatabaseServer()
            }
        }
    }
}