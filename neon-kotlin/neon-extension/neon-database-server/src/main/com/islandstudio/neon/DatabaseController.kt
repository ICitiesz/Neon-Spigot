package com.islandstudio.neon

import com.islandstudio.neon.shared.PluginAdapter
import com.islandstudio.neon.shared.core.AppContext
import com.islandstudio.neon.shared.core.config.AppConfig
import com.islandstudio.neon.shared.core.config.wrapper.NeonDBConfigWrapper
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.io.folder.NeonDataFolder
import com.islandstudio.neon.shared.core.io.resource.NeonExternalResource
import kotlinx.coroutines.*
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.hsqldb.DatabaseManager
import org.hsqldb.server.Server
import org.jooq.Configuration
import org.koin.core.annotation.Single
import org.koin.core.component.inject
import java.io.File
import java.sql.DriverManager

@Single
class DatabaseController: IComponentInjector {
    private val appContext by inject<AppContext>()
    private val pluginAdapter by inject<PluginAdapter<NeonDatabaseServer>>()
    private val neonDbServer = pluginAdapter.plugin
    private val dbContextConfig by inject<Configuration>()
    private val hsqldbServer by inject<Server>()
    private val neonDBConfig by lazy {
        AppConfig(NeonExternalResource.NeonDBServerConfigFile2, NeonDBConfigWrapper())
    }
    private val neonDBConfigWrapper = neonDBConfig.configWrapper
    private val isUniversal = neonDBConfigWrapper.getImmutableConfigObject().neonDBConfig.isUniversal
    private val dbFolderName = when(isUniversal) {
        true -> "NeonDB-${appContext.serverRunningMode.name}"

        else -> "NeonDB-${appContext.serverMajorVersion}-${appContext.serverRunningMode.name}"
    }
    private val dbFolderPath = "${NeonDataFolder.NeonDatabaseFolder.path}${File.separator}${dbFolderName}${File.separator}${dbFolderName}"

    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    fun initDatabaseServer() {
        if (isHsqlDbServerRunning()) return

        val jobContext = newSingleThreadContext("Neon Database Initializer")

        jobContext.use {
            CoroutineScope(it).async {
                Class.forName(appContext.getAppEnvValue("DATABASE_DRIVER"))

                println(appContext.getAppEnvValue("DATABASE_ALIAS"))

                hsqldbServer.apply {
                    this.logWriter = null
                    this.errWriter = null
                    this.setDatabaseName(0, appContext.getAppEnvValue("DATABASE_ALIAS"))
                    this.setDatabasePath(0, "file:${dbFolderPath}")
                    this.isNoSystemExit = true
                    this.address = appContext.getAppEnvValue("DATABASE_ADDRESS")
                }

                hsqldbServer.start()

            }.invokeOnCompletion {
                if (!isHsqlDbServerRunning()) {
                    neonDbServer.logger.severe(appContext.getCodeMessage("neon_database_server.error.db_server_start_failed"))

                    it?.printStackTrace()

                    return@invokeOnCompletion
                }

                neonDbServer.logger.info(appContext.getCodeMessage("neon_database_server.info.db_server_start_success"))

                // Disable logger for `org.jooq.Constants`
                System.setProperty("org.jooq.no-logo", "true")
                System.setProperty("org.jooq.no-tips", "true")



                updateDatabaseStructure()
            }
        }
    }

    private fun isHsqlDbServerRunning(): Boolean {
        return !hsqldbServer.isNotRunning
    }

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    fun stopDbServer() {
        if (!isHsqlDbServerRunning()) return

        val jobContext = newSingleThreadContext("Neon Database Processor")

        CoroutineScope(jobContext).launch {
            neonDbServer.logger.info("Closing Neon database......")
            DatabaseManager.closeDatabases(0)
            hsqldbServer.shutdown()
            //NeonAPI.disconnectDataSource()
            //hikariDataSource?.close()
            hsqldbServer.stop()
        }.invokeOnCompletion {
            neonDbServer.logger.info("Neon Database closed!")
            jobContext.close()
        }
    }

    fun updateDatabaseStructure() {
        CoroutineScope(Dispatchers.IO).async {
            delay(1000)

            val database = DatabaseFactory
                .getInstance()
                .findCorrectDatabaseImplementation(
                    JdbcConnection(DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/neondb", "SA", ""))
                ).apply {
                    this.defaultCatalogName = appContext.getAppEnvValue("DATABASE_CATALOG")
                    this.defaultSchemaName = appContext.getAppEnvValue("DATABASE_DEFAULT_SCHEMA")
                }

            val liquibase = Liquibase("resources/application/neon-database-changelog.xml",
                ClassLoaderResourceAccessor(this::class.java.classLoader), database)

            liquibase.use {
                it.update()
            }

            database.connection.close()
        }
    }
}