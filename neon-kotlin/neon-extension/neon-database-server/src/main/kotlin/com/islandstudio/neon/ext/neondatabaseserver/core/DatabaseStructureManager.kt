package com.islandstudio.neon.ext.neondatabaseserver.core

import com.islandstudio.neon.ext.neondatabaseserver.NeonDatabaseServer
import com.islandstudio.neon.shared.PluginAdapter
import com.islandstudio.neon.shared.core.AppContext
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.io.resource.NeonInternalResource
import com.islandstudio.neon.shared.datasource.IDatabaseContext
import kotlinx.coroutines.*
import liquibase.Liquibase
import liquibase.UpdateSummaryEnum
import liquibase.UpdateSummaryOutputEnum
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.koin.core.annotation.Single
import org.koin.core.component.inject

@Single
class DatabaseStructureManager: IComponentInjector, IDatabaseContext {
    private val appContext by inject<AppContext>()
    private val pluginAdapter by inject<PluginAdapter<NeonDatabaseServer>>()

    fun updateStructure() {
        CoroutineScope(Dispatchers.IO).async {
            pluginAdapter.getPluginLogger()
                .info(appContext.getCodeMessage("neon_database_server.info.update_db_structure"))

            delay(100)

            /* Get database instance of Liquibase */
            val databaseInstance = DatabaseFactory
                .getInstance()
                .findCorrectDatabaseImplementation(JdbcConnection(dbConnection()))
                .apply {
                    this.defaultCatalogName = appContext.getAppEnvValue("DATABASE_CATALOG")
                    this.defaultSchemaName = appContext.getAppEnvValue("DATABASE_DEFAULT_SCHEMA")
                }

            val liquibaseInstance = Liquibase(
                NeonInternalResource.NeonDBServerUpdateScript.resourceURL,
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