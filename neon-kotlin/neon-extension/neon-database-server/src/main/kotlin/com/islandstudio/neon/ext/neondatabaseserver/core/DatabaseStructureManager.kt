package com.islandstudio.neon.ext.neondatabaseserver.core

import com.islandstudio.neon.ext.neondatabaseserver.NeonDatabaseServer
import com.islandstudio.neon.shared.PluginAdapter
import com.islandstudio.neon.shared.core.AppContext
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.io.resource.NeonInternalResource
import com.islandstudio.neon.shared.datasource.IDatabaseContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import liquibase.Liquibase
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
            delay(200)
            pluginAdapter.getPluginLogger().info(appContext.getCodeMessage("neon_database_server.info.update_db_structure"))

            /* Get database instance of Liquibase */
            val databaseIntsance = DatabaseFactory
                .getInstance()
                .findCorrectDatabaseImplementation(JdbcConnection(dbConnection()))
                .apply {
                    this.defaultCatalogName = appContext.getAppEnvValue("DATABASE_CATALOG")
                    this.defaultSchemaName = appContext.getAppEnvValue("DATABASE_DEFAULT_SCHEMA")
                    this.isAutoCommit = true
                }

            databaseIntsance
                .use {
                    val liquibase = Liquibase(
                        NeonInternalResource.NeonDBServerUpdateScript.resourceURL,
                        ClassLoaderResourceAccessor(this.javaClass.classLoader),
                        it
                    )

                    liquibase.use {
                        it.update()
                    }
                }
        }.invokeOnCompletion {
            pluginAdapter.getPluginLogger().info(appContext.getCodeMessage("neon_database_server.info.update_db_structure_success"))
        }
    }
}