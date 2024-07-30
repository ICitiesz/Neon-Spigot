package com.islandstudio.neon

import com.islandstudio.neon.application.AppContext
import com.islandstudio.neon.stable.core.init.NConstructor
import com.islandstudio.neon.stable.core.io.nFile.NFile
import com.islandstudio.neon.stable.core.io.nFile.NeonDataFolder
import kotlinx.coroutines.*
import org.hsqldb.DatabaseManager
import org.hsqldb.server.Server
import org.koin.core.component.inject
import org.simpleyaml.configuration.file.YamlFile
import org.simpleyaml.utils.SupplierIO
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

object DatabaseController: AppContext.Injector {
    private val dbExtension by inject<NeonDatabaseExtension>()
    private val hsqldbServer by inject<Server>()

    private val dbConfigFile = NFile.createOrGetNewFile(
        NeonDataFolder.NeonDatabaseFolder(),
        File(NeonDataFolder.NeonDatabaseFolder(), "database-config.yml")
    )

    /* Determine which scope that database should be generated */
    private val isGlobal by lazy { YamlFile.loadConfiguration(SupplierIO.Reader { dbConfigFile.reader() })
        .getBoolean("NeonDatabase.isGlobal") }

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    fun initDatabaseServer() {
        initDatabaseConfig()

        if (isDbServerRunning()) return

        val jobContext = newSingleThreadContext("Neon Database Initializer")

        CoroutineScope(jobContext).async {
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
            if (!isDbServerRunning()) {
                return@invokeOnCompletion dbExtension.logger.severe(AppContext.getCodeMessages("neon_database.error.db_server_start_failed"))
            }

            dbExtension.logger.info(AppContext.getCodeMessages("neon_database.info.db_connection_success"))

            // Disable logger for `org.jooq.Constants`
            System.setProperty("org.jooq.no-logo", "true")
            System.setProperty("org.jooq.no-tips", "true")
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    fun stopDbServer() {
        if (!isDbServerRunning()) return

        val jobContext = newSingleThreadContext("Neon Database Processor")

        CoroutineScope(jobContext).launch {
            dbExtension.logger.info("Closing Neon database......")
            DatabaseManager.closeDatabases(0)
            hsqldbServer.shutdown()
            //hikariDataSource?.close()
            hsqldbServer.stop()
        }.invokeOnCompletion {
            dbExtension.logger.info("Neon Database closed!")
            jobContext.close()
        }
    }

    //fun getDataSource(): HikariDataSource? = hikariDataSource

    private fun isDbServerRunning(): Boolean = !hsqldbServer.isNotRunning

    private fun initDatabaseConfig() {
        with(dbConfigFile) {
            if (this.length() != 0L) return@with

            dbExtension.getPluginClassLoader().getResourceAsStream("resources/database-config.yml")?.let {
                Files.copy(it, dbConfigFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
        }
    }

    private fun getDatabaseFolderName(): String {
        return when {
            isGlobal -> {
                "Neon-Global-${NConstructor.serverRunningMode.value .replaceFirstChar { chr -> chr.uppercaseChar() }}"
            }

            else -> {
                "Neon-${NConstructor.getMajorVersion()}-${NConstructor.serverRunningMode.value.replaceFirstChar { chr -> chr.uppercaseChar() }}"
            }
        }
    }

    private fun getDatabaseFolderPath(): String {
        return when {
            isGlobal -> {
                "${NeonDataFolder.NeonDatabaseFolder().path}${File.separator}${getDatabaseFolderName()}${File.separator}${getDatabaseFolderName()}-DB"
            }

            else -> {
                "${NeonDataFolder.NeonDatabaseFolder().path}${File.separator}${getDatabaseFolderName()}${File.separator}${getDatabaseFolderName()}-DB"
            }
        }
    }
}