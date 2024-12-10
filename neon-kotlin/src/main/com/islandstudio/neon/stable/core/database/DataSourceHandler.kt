package com.islandstudio.neon.stable.core.database

import com.islandstudio.neon.stable.core.application.AppContext
import com.islandstudio.neon.stable.core.application.di.ModuleInjector
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.koin.core.component.inject


class DataSourceHandler: ModuleInjector {
    private val appContext by inject<AppContext>()
    private val hikariConfig: HikariConfig = HikariConfig().apply {
        // TODO: May let user decide the databRase alias name (jdbc:hsqldb:hsql://localhost/{aliasName}) in the future.
        this.jdbcUrl = "jdbc:hsqldb:hsql://localhost/neondatabase"
        this.driverClassName = "org.hsqldb.jdbc.JDBCDriver"
        this.username = appContext.getAppEnvValue("DATABASE_USERNAME")
        this.password = appContext.getAppEnvValue("DATABASE_PASSWORD")
        this.maximumPoolSize = 20
        this.poolName = "Neon Database Pool"
        this.isAutoCommit = true
    }

    fun getHikariDataSource(): HikariDataSource = HikariDataSource(hikariConfig)

    fun getHikariConfig(): HikariConfig = hikariConfig
}