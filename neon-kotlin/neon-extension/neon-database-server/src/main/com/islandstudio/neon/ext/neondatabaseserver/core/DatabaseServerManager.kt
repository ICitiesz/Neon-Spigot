package com.islandstudio.neon.ext.neondatabaseserver.core

import com.islandstudio.neon.ext.neondatabaseserver.NeonDatabaseServer
import com.islandstudio.neon.shared.PluginAdapter
import com.islandstudio.neon.shared.core.AppContext
import com.islandstudio.neon.shared.core.config.AppConfig
import com.islandstudio.neon.shared.core.config.wrapper.NeonDBConfigWrapper
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.exception.NeonLoaderException
import com.islandstudio.neon.shared.core.io.folder.NeonDataFolder
import com.islandstudio.neon.shared.core.io.resource.NeonExternalResource
import com.islandstudio.neon.shared.datasource.ConnectionManager
import kotlinx.coroutines.*
import org.hsqldb.DatabaseManager
import org.hsqldb.server.Server
import org.koin.core.annotation.Single
import org.koin.core.component.inject
import java.io.File

@Single
class DatabaseServerManager: IComponentInjector {
    private val pluginAdapter by inject<PluginAdapter<NeonDatabaseServer>>()
    private val appContext by inject<AppContext>()
    private val databaseServer by inject<Server>()
    private val databaseConfigHandler by lazy {
        AppConfig(NeonExternalResource.NeonDBServerConfigFile2,
            NeonDBConfigWrapper()
        )
    }
    private val databaseConfig = databaseConfigHandler.configWrapper.getImmutableConfigObject().neonDBConfig

    /* Database properties */
    private val dbFolderName = when(databaseConfig.isUniversal) {
        true -> "NeonDB-${appContext.serverRunningMode.name}"

        else -> "NeonDB-${appContext.serverMajorVersion}-${appContext.serverRunningMode.name}"
    }
    private val dbFolderPath = "${NeonDataFolder.NeonDatabaseFolder.path}${File.separator}${dbFolderName}${File.separator}${dbFolderName}"

    val connectionManager by inject<ConnectionManager>()
    val databaseStructureManager by inject<DatabaseStructureManager>()

    /**
     * Initialize database server
     *
     */
    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    fun initializeDatabaseServer() {
        if (isServerRunning()) return

        newSingleThreadContext("[NDS] Database Server Manager").use { dispatcher ->
            pluginAdapter.getPluginLogger().info(appContext.getCodeMessage("neon_database_server.info.initialize_db_server"))

            CoroutineScope(dispatcher).async {
                /* Load HSQLDB driver */
                runCatching {
                    Class.forName(appContext.getAppEnvValue("DATABASE_DRIVER"))
                }.onFailure {
                    if (it !is ClassNotFoundException) {
                        throw NeonLoaderException(it.message)
                    }

                    throw NeonLoaderException("neon_database_server.error.db_driver_not_found")
                }

                /* Setting up server with define properties */
                databaseServer.apply {
                    this.logWriter = null
                    this.errWriter = null
                    this.setDatabaseName(0, appContext.getAppEnvValue("DATABASE_ALIAS"))
                    this.setDatabasePath(0, "file:${dbFolderPath}")
                    this.isNoSystemExit = true
                    this.address = appContext.getAppEnvValue("DATABASE_ADDRESS")

                    pluginAdapter.getPluginLogger().info(appContext.getCodeMessage("neon_database_server.info.start_db_server"))
                    this.start()
                }
            }.invokeOnCompletion {
                if (!isServerRunning()) {
                    it?.printStackTrace()
                    throw NeonLoaderException(appContext.getCodeMessage("neon_database_server.error.db_server_start_failed"))
                }

                pluginAdapter.getPluginLogger().info(appContext.getCodeMessage("neon_database_server.info.start_db_server_success"))

                /* Disable logger for `org.jooq.Constants` */
                System.setProperty("org.jooq.no-logo", "true")
                System.setProperty("org.jooq.no-tips", "true")

                connectionManager.establishConnection()
                databaseStructureManager.updateStructure()
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    fun stopDatabaseServer() {
        if (!isServerRunning()) return

        newSingleThreadContext("[NDS] Database Server Manager").use { dispatcher ->
            CoroutineScope(dispatcher).launch {
                pluginAdapter.getPluginLogger().info("Closing Neon database......")
                DatabaseManager.closeDatabases(0)
                databaseServer.shutdown()
                //NeonAPI.disconnectDataSource()
                //hikariDataSource?.close()
                databaseServer.stop()
            }.invokeOnCompletion {
                pluginAdapter.getPluginLogger().info("Neon Database closed!")
            }
        }
    }

    private fun isServerRunning(): Boolean {
        return !databaseServer.isNotRunning
    }
}