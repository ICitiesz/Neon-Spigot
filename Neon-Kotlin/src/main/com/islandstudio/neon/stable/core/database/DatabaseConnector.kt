package com.islandstudio.neon.stable.core.database

import com.islandstudio.neon.stable.core.application.AppContext
import com.islandstudio.neon.stable.core.application.NeonExtensions
import com.islandstudio.neon.stable.core.application.di.ModuleInjector
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DatabaseConnector: ModuleInjector {
    /* Database Properties */
    val hikariConfig by lazy { HikariConfig() }
    val hikariDataSource by lazy { HikariDataSource(hikariConfig) }

    /**
     * Establish database connection
     *
     */
    fun run() {
        CoroutineScope(Dispatchers.IO).launch {
            AppContext.loadExtension(NeonExtensions.NeonDatabaseExtension())
        }.invokeOnCompletion {
            hikariConfig.apply {
                // TODO: May let user decide the databRase alias name (jdbc:hsqldb:hsql://localhost/{aliasName}) in the future.
                this.jdbcUrl = "jdbc:hsqldb:hsql://localhost/neondatabase"
                this.driverClassName = "org.hsqldb.jdbc.JDBCDriver"
                this.username = AppContext.getAppEnvValue("DATABASE_USERNAME")
                this.password = AppContext.getAppEnvValue("DATABASE_PASSWORD")
                this.maximumPoolSize = 20
            }.also {
                hikariDataSource
            }
        }
    }
}