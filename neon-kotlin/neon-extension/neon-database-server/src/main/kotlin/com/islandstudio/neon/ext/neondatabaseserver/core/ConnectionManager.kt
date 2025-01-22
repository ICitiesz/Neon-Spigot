package com.islandstudio.neon.ext.neondatabaseserver.core

import com.islandstudio.neon.shared.PluginAdapter
import com.islandstudio.neon.shared.core.AppContext
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.exception.NeonConnectionException
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.annotation.Single
import org.koin.core.component.inject

@Single
class ConnectionManager: IComponentInjector {
    private val pluginAdapter by inject<PluginAdapter<JavaPlugin>>()
    private val appContext by inject<AppContext>()
    private val dataSourceWrapper = DataSourceWrapper()
    private lateinit var hikariDataSource: HikariDataSource

    /**
     * Establish database connection using the data source.
     *
     */
    fun establishConnection() {
        pluginAdapter.getPluginLogger().info(appContext.getCodeMessage("neon_database_server.info.establish_db_connection"))

        runCatching {
            hikariDataSource = dataSourceWrapper.initDataSource()
        }.onFailure {
            throw NeonConnectionException(it.cause?.message)
        }.onSuccess {
            pluginAdapter.getPluginLogger().info(appContext.getCodeMessage("neon_database_server.info.establish_db_connection_success"))
        }
    }

    /**
     * Close database connection.
     *
     */
    fun closeConnection() {
        pluginAdapter.getPluginLogger().info(appContext.getCodeMessage("neon_database_server.info.close_db_connection"))

        runCatching {
            hikariDataSource.close()
        }.onFailure {
            throw NeonConnectionException(it.cause?.message)
        }.onSuccess {
            pluginAdapter.getPluginLogger().info(appContext.getCodeMessage("neon_database_server.info.close_db_connection_success"))
        }
    }

    fun getDataSourceConfig(): HikariConfig = dataSourceWrapper.getHikariConfig()

    fun getDataSource(): HikariDataSource = hikariDataSource
}