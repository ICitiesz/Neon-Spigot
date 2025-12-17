package com.islandstudio.neon.apinew.connection

import ch.vorburger.mariadb4j.DB
import ch.vorburger.mariadb4j.DBConfigurationBuilder
import com.islandstudio.neon.shared.core.di.IComponentProvider
import com.islandstudio.neon.shared.experimental.NeonDataFolderNew
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class NeonDatabaseManager: IComponentProvider {
    private var dataSource: NeonDataSource? = null
    private var databaseContext: NeonDatabaseContext? = null
    private var dbCore: DB? = null

    suspend fun initialize() {
        withContext(Dispatchers.IO) {
            async {
                val dbConfig = DBConfigurationBuilder.newBuilder().apply {
                    this.setPort(3306)
                    this.setDataDir(NeonDataFolderNew.NeonDatabaseDataFolder)
                    this.setBaseDir(NeonDataFolderNew.NeonDatabaseCoreFolder)
                }

                dbCore = DB.newEmbeddedDB(dbConfig.build()).apply {
                    start()
                    createDB("neon_online_preview")
                    source("resources/database/NeonDatabaseInit.sql")
                }
            }.await()

            dataSource = dbCore?.let { db ->
                NeonDataSource.create(
                    db.configuration.getURL("neon_online_preview"),
                    "root"
                )
            }

            databaseContext = dataSource?.let { neonDataSource ->
                NeonDatabaseContext(neonDataSource)
            }?.apply {
                getKoin().declare(this)
            }
        }
    }

    fun close() {
        dataSource?.close()
        dbCore?.stop()
    }
}