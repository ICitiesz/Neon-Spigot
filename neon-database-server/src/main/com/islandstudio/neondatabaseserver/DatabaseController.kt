package com.islandstudio.neondatabaseserver

import com.islandstudio.neon.stable.core.application.extension.NeonAPI
import com.islandstudio.neon.stable.core.application.init.NConstructor
import com.islandstudio.neon.stable.core.io.nFile.NeonDataFolder
import com.islandstudio.neondatabaseserver.application.AppContext
import com.islandstudio.neondatabaseserver.application.di.ModuleInjector
import kotlinx.coroutines.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.Filter
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.filter.RegexFilter
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.logging.LogFactory
import org.hsqldb.DatabaseManager
import org.hsqldb.server.Server
import org.koin.core.component.inject
import org.simpleyaml.configuration.file.YamlFile
import org.simpleyaml.utils.SupplierIO
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

object DatabaseController: ModuleInjector {
    private val appContext by inject<AppContext>()
    private val neonDbServer by inject<NeonDatabaseServer>()
    private val hsqldbServer by inject<Server>()

    private val dbConfigFile = NeonDataFolder.createNewFile(NeonDataFolder.NeonDatabaseFolder, "database-config.yml")

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
                return@invokeOnCompletion neonDbServer.logger.severe(appContext.getCodeMessage("neon_database_server.error.db_server_start_failed"))
            }

            neonDbServer.logger.info(appContext.getCodeMessage("neon_database_server.info.db_server_start_success"))

            // Disable logger for `org.jooq.Constants`
            System.setProperty("org.jooq.no-logo", "true")
            System.setProperty("org.jooq.no-tips", "true")

            updateDatabaseStructure()

            jobContext.close()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    fun stopDbServer() {
        if (!isDbServerRunning()) return

        val jobContext = newSingleThreadContext("Neon Database Processor")

        CoroutineScope(jobContext).launch {
            neonDbServer.logger.info("Closing Neon database......")
            DatabaseManager.closeDatabases(0)
            hsqldbServer.shutdown()
            //hikariDataSource?.close()
            hsqldbServer.stop()
        }.invokeOnCompletion {
            neonDbServer.logger.info("Neon Database closed!")
            jobContext.close()
        }
    }

    //fun getDataSource(): HikariDataSource? = hikariDataSource

    private fun isDbServerRunning(): Boolean = !hsqldbServer.isNotRunning

    private fun initDatabaseConfig() {
        with(dbConfigFile) {
            if (this.length() != 0L) return@with

            neonDbServer.getPluginClassLoader().getResourceAsStream("resources/database-config.yml")?.let {
                Files.copy(it, dbConfigFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
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

//    private fun getDatabaseFolderPath(): String {
//        return when {
//            isGlobal -> {
//                "${NeonDataFolder.NeonDatabaseFolder().path}${File.separator}${getDatabaseFolderName()}${File.separator}${getDatabaseFolderName()}-DB"
//            }
//
//            else -> {
//                "${NeonDataFolder.NeonDatabaseFolder().path}${File.separator}${getDatabaseFolderName()}${File.separator}${getDatabaseFolderName()}-DB"
//            }
//        }
//    }

    //TODO: Credential will be store in .env file
    /**
     * Update database structure based on the provided update scripts if available.
     *
     * Update script file name pattern: v{version}-db_udpate.sql
     * Details will be within the scripts itself by using comment.
     */
    private fun updateDatabaseStructure() {
        val flywayDbMigrator = Flyway.configure(neonDbServer.getPluginClassLoader())
            .locations(appContext.getAppEnvValue("DATABASE_UPDATE_SCRIPTS_LOCATION"))
            .dataSource(
                "jdbc:hsqldb:hsql://localhost/neondatabase",
                "SA",
                ""
            )
            .driver(appContext.getAppEnvValue("DATABASE_DRIVER"))
            .defaultSchema("NEON_DATA") // TODO: Will change to other schema for storing flyway migration index
            .validateMigrationNaming(true)
            .sqlMigrationPrefix(appContext.getAppEnvValue("FLYWAY_SQL_MIGRATION_PREFIX"))
            .sqlMigrationSeparator(appContext.getAppEnvValue("FLYWAY_SQL_MIGRATION_SEPARATOR"))
            .loggers(appContext.getAppEnvValue("FLYWAY_LOGGER"))
            .baselineVersion("0")
            .load().also {
                suppressAuthReq()
            }

        flywayDbMigrator.baseline()
        flywayDbMigrator.migrate()
    }

    /**
     * Suppress authentication notification upon database migration/updates.
     */
    private fun suppressAuthReq() {
        val loggerContext = LogManager.getContext(LogFactory::class.java.getClassLoader(), false) as LoggerContext
        val regexFilter = RegexFilter.createFilter("^You are not signed in to Flyway, to sign in please run auth"
            , arrayOf("CASE_INSENSITIVE"), true, Filter.Result.DENY, Filter.Result.ACCEPT)

        if (!loggerContext.rootLogger.filters.asSequence().toList().contains(regexFilter)) {
            loggerContext.addFilter(regexFilter)
        }

        if (!regexFilter.isStarted) regexFilter.start()
    }
}