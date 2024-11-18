package com.islandstudio.neon.stable.core.database

import com.islandstudio.neon.Neon
import com.islandstudio.neon.stable.core.application.AppContext
import com.islandstudio.neon.stable.core.application.di.ModuleInjector
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.inject

object DatabaseConnector: ModuleInjector {
    /* Database Properties */
    val hikariConfig by lazy { HikariConfig() }
    val hikariDataSource by lazy { HikariDataSource(hikariConfig) }
    val appContext by inject<AppContext>()
    val neon by inject<Neon>()

    /**
     * Establish database connection
     *
     */
    fun run() {
        CoroutineScope(Dispatchers.IO).launch {
            hikariConfig.apply {
                // TODO: May let user decide the databRase alias name (jdbc:hsqldb:hsql://localhost/{aliasName}) in the future.
                this.jdbcUrl = "jdbc:hsqldb:hsql://localhost/neondatabase"
                this.driverClassName = "org.hsqldb.jdbc.JDBCDriver"
                this.username = appContext.getAppEnvValue("DATABASE_USERNAME")
                this.password = appContext.getAppEnvValue("DATABASE_PASSWORD")
                this.maximumPoolSize = 20
            }.also {
                hikariDataSource
            }
        }.invokeOnCompletion {
            if (it !is Exception) {
                neon.logger.info("Database connection established!")
            }
        }
    }
}