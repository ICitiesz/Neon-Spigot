package com.islandstudio.neon.ext.neondatabaseserver.core

import com.islandstudio.neon.shared.core.AppContext
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.koin.core.component.inject

class DataSourceWrapper: IComponentInjector {
    private val appContext by inject<AppContext>()
    private val hikariConfig by lazy {
        HikariConfig().apply {
            // TODO: May let user decide the databRase alias name (jdbc:hsqldb:hsql://localhost/{aliasName}) in the future.
            this.jdbcUrl = "jdbc:hsqldb:hsql://${appContext.getAppEnvValue("DATABASE_ADDRESS")}/${appContext.getAppEnvValue("DATABASE_ALIAS")}"
            this.driverClassName = appContext.getAppEnvValue("DATABASE_DRIVER")
            this.username = appContext.getAppEnvValue("DATABASE_USERNAME")
            this.password = appContext.getAppEnvValue("DATABASE_PASSWORD")
            this.maximumPoolSize = 20
            this.poolName = "Neon Database Pool"
            this.isAutoCommit = true
        }
    }

    private val hikariDataSource by lazy { HikariDataSource(hikariConfig) }

    @JvmName("getHikariCfg")
    fun getHikariConfig(): HikariConfig = hikariConfig

    fun initDataSource(): HikariDataSource = hikariDataSource
}