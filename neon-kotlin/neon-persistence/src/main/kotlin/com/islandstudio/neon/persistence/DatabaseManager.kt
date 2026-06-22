package com.islandstudio.neon.persistence

import ch.vorburger.mariadb4j.DB
import ch.vorburger.mariadb4j.DBConfigurationBuilder
import com.islandstudio.neon.shared.rework.core.di.IComponentProvider
import com.islandstudio.neon.shared.rework.core.di.getKoinContext
import com.islandstudio.neon.shared.rework.core.initialization.context.IPluginContext
import com.islandstudio.neon.shared.rework.core.initialization.runner.IRunnerAsync
import com.islandstudio.neon.shared.rework.core.io.DataDirectory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.jdbc.Database

class DatabaseManager(private val pluginContext: IPluginContext): IRunnerAsync, IComponentProvider {
    private var dataSource: DataSource? = null
    private var dbCore: DB? = null
    private var databaseContext: DatabaseContext? = null

    override suspend fun runSuspend() {
        withContext(Dispatchers.IO) {
            try {
                pluginContext.getPluginLogger().info("Initializing database manager......")

                val dbConfig  = DBConfigurationBuilder.newBuilder().apply {
                    this.setPort(3306)
                    this.setDataDir(DataDirectory.NeonDatabaseDataFolder)
                    this.setBaseDir(DataDirectory.NeonDatabaseCoreFolder)
                }

                dbCore = DB.newEmbeddedDB(dbConfig.build()).apply {
                    start()
                    createDB("neon_online_preview")
                    source("resources/NeonDatabaseInit.sql")
                }

                dataSource = dbCore?.let { db ->
                    DataSource.create(
                        db.configuration.getURL("neon_online_preview") + "?allowMultiQueries=true",
                        ""
                    )
                }?.apply {
                    val exposedDatabase by lazy {
                        Database.connect(this)
                    }

                    databaseContext = DatabaseContext(exposedDatabase)

                    getKoinContext().declare(databaseContext)
                }
            } catch (ex: Exception) {
                //TODO: Need proper error handling
                ex.printStackTrace()
            }
        }
    }

    fun close() {
        dataSource?.close()
        dbCore?.stop()
    }
}