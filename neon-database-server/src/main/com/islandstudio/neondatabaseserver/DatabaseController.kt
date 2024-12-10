package com.islandstudio.neondatabaseserver

import com.islandstudio.neon.stable.core.application.extension.NeonAPI
import com.islandstudio.neon.stable.core.application.init.NConstructor
import com.islandstudio.neon.stable.core.io.nFile.NeonDataFolder
import com.islandstudio.neon.stable.core.io.resource.NeonExternalResources
import com.islandstudio.neondatabaseserver.application.AppContext
import com.islandstudio.neondatabaseserver.application.di.ModuleInjector
import kotlinx.coroutines.*
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.hsqldb.DatabaseManager
import org.hsqldb.server.Server
import org.jooq.Configuration
import org.koin.core.component.inject
import org.simpleyaml.configuration.file.YamlFile
import org.simpleyaml.utils.SupplierIO
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.sql.DriverManager

class DatabaseController: ModuleInjector {
    private val appContext by inject<AppContext>()
    private val neonDbServer by inject<NeonDatabaseServer>()
    private val dbContextConfig by inject<Configuration>()
    private val hsqldbServer by inject<Server>()
    private val dbConfigFile = NeonDataFolder.createNewFile(NeonExternalResources.NeonDatabaseServerConfigFile)

    // TODO: Need merge into `isMcVersionBased` where the database is init based on mc version or as universal
    private val isGlobal by lazy { YamlFile.loadConfiguration(SupplierIO.Reader { dbConfigFile.reader() })
        .getBoolean("NeonDatabase.isGlobal") }
    private val isMcVersionBased by lazy { false }

    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    fun initDatabaseServer() {
        initDatabaseConfig()

        if (isHsqlDbServerRunning()) return

        val jobContext = newSingleThreadContext("Neon Database Initializer")

        jobContext.use {
            CoroutineScope(it).async {
                Class.forName("org.hsqldb.jdbc.JDBCDriver")

                hsqldbServer.apply {
                    this.logWriter = null
                    this.errWriter = null
                    this.setDatabaseName(0, "neondatabase")
                    this.setDatabasePath(0, "file:${getDatabaseFolderPath().split("jdbc:hsqldb:hsql:").last()}")
                    this.isNoSystemExit = true
                    this.address = "localhost"
                }

                hsqldbServer.start()

            }.invokeOnCompletion {
                if (!isHsqlDbServerRunning()) {
                    return@invokeOnCompletion neonDbServer.logger.severe(appContext.getCodeMessage("neon_database_server.error.db_server_start_failed"))
                }

                neonDbServer.logger.info(appContext.getCodeMessage("neon_database_server.info.db_server_start_success"))


                // Disable logger for `org.jooq.Constants`
                System.setProperty("org.jooq.no-logo", "true")
                System.setProperty("org.jooq.no-tips", "true")

                updateDatabaseStructure()
            }
        }
    }

    private fun getDatabaseFolderName(): String {
        return when {
            isGlobal -> {
                "Neon-Global-${NeonAPI.getServerRunningMode().value.replaceFirstChar { chr -> chr.uppercaseChar() }}"
            }

            else -> {
                "Neon-${NConstructor.getMajorVersion()}-${NeonAPI.getServerRunningMode().value.replaceFirstChar { chr -> chr.uppercaseChar() }}"
            }
        }
    }

    private fun getDatabaseFolderPath(): String = "${NeonDataFolder.NeonDatabaseFolder.path}${File.separator}${getDatabaseFolderName()}${File.separator}${getDatabaseFolderName()}-DB"

    private fun initDatabaseConfig() {
        with(dbConfigFile) {
            if (this.length() != 0L) return@with

            neonDbServer.getPluginClassLoader().getResourceAsStream("resources/database-config.yml")?.let {
                Files.copy(it, dbConfigFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
        }
    }

    fun isHsqlDbServerRunning(): Boolean {
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
                    JdbcConnection(DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/neondatabase", "SA", ""))
                ).apply {
                    this.defaultCatalogName = ""
                }
            database.defaultCatalogName = "NEON_DB"
            database.defaultSchemaName = "PUBLIC"

            val liquibase = Liquibase("resources/application/neon-database-changelog.xml",
                ClassLoaderResourceAccessor(this::class.java.classLoader), database)

            liquibase.use {
                it.update()
            }

            database.connection.close()
        }
    }
}