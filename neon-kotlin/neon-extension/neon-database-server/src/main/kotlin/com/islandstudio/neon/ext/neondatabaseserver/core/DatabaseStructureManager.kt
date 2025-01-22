package com.islandstudio.neon.ext.neondatabaseserver.core

import com.islandstudio.neon.ext.neondatabaseserver.NeonDatabaseServer
import com.islandstudio.neon.shared.PluginAdapter
import com.islandstudio.neon.shared.core.AppContext
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.io.resource.NeonInternalResource
import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture
import liquibase.Liquibase
import liquibase.UpdateSummaryEnum
import liquibase.UpdateSummaryOutputEnum
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.koin.core.annotation.Single
import org.koin.core.component.inject

@Single
class DatabaseStructureManager: IComponentInjector {
    private val appContext by inject<AppContext>()
    private val pluginAdapter by inject<PluginAdapter<NeonDatabaseServer>>()
    private val connectionManager by inject<ConnectionManager>()

    fun updateStructure() {
        CoroutineScope(Dispatchers.Default).async {
            pluginAdapter.getPluginLogger()
                .info(appContext.getCodeMessage("neon_database_server.info.update_db_structure"))

            delay(100)

                this.async {
                    connectionManager.getDataSource().connection.use { conn ->
                        val statement = conn.createStatement()

                        val createDefaultSchema = "CREATE SCHEMA IF NOT EXISTS \"${appContext.getAppEnvValue("DATABASE_SCHEMA")}\""
                        val createDBUpdateSchema = "CREATE SCHEMA IF NOT EXISTS \"${appContext.getAppEnvValue("DATABASE_DB_UPDATE_SCHEMA")}\""

                        val alterCatalog = """
                            ALTER CATALOG "${appContext.getAppEnvValue("DATABASE_PUBLIC_CATALOG")}" 
                            RENAME TO "${appContext.getAppEnvValue("DATABASE_CATALOG")}"
                        """.trimIndent()
                        val checkPublicCatalog = """
                            SELECT COUNT(*) FROM INFORMATION_SCHEMA.INFORMATION_SCHEMA_CATALOG_NAME
                            WHERE CATALOG_NAME = '${appContext.getAppEnvValue("DATABASE_PUBLIC_CATALOG")}'
                        """.trimIndent()

                        statement.use { stmt ->
                            val result = stmt.executeQuery(checkPublicCatalog)

                            if (result.next()) {
                                val publicCatalogCount =  result.getInt(1)

                                if (publicCatalogCount == 1) {
                                    stmt.execute(alterCatalog)
                                }
                            }

                            stmt.execute(createDefaultSchema)
                            stmt.execute(createDBUpdateSchema)
                        }
                    }
                }.asCompletableFuture().join()

            /* Get database instance of Liquibase */
            val databaseInstance = DatabaseFactory
                .getInstance()
                .findCorrectDatabaseImplementation(JdbcConnection(connectionManager.getDataSource().connection))
                .apply {
                    this.databaseChangeLogTableName = appContext.getAppEnvValue("DATABASE_DB_CHANGES_TABLE_NAME")
                    this.databaseChangeLogLockTableName = appContext.getAppEnvValue("DATABASE_DB_CHANGES_LOCK_TABLE_NAME")
                    this.defaultCatalogName = appContext.getAppEnvValue("DATABASE_CATALOG")
                    this.defaultSchemaName = appContext.getAppEnvValue("DATABASE_DB_UPDATE_SCHEMA")
                }

            val liquibaseInstance = Liquibase(
                NeonInternalResource.NeonDBInitScript.resourceURL,
                ClassLoaderResourceAccessor(this.javaClass.classLoader),
                databaseInstance
            ).apply {
                this.setShowSummary(UpdateSummaryEnum.SUMMARY)
                this.setShowSummaryOutput(UpdateSummaryOutputEnum.CONSOLE)
            }

            liquibaseInstance.use {
                it.update()
            }

            joinAll()
        }.invokeOnCompletion {
            pluginAdapter.getPluginLogger().info(appContext.getCodeMessage("neon_database_server.info.update_db_structure_success"))
        }
    }
}