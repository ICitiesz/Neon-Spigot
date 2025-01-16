package com.islandstudio.neon.stable.core.database

import com.islandstudio.neon.Neon
import com.islandstudio.neon.shared.core.AppContext
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.koin.core.annotation.Single
import org.koin.core.component.inject

@Single
class DatabaseInterface: IComponentInjector {
    private val neon by inject<Neon>()
    private val appContext by inject<AppContext>()
    private val dataSourceHandler = DataSourceHandler()
    private lateinit var dataSource: HikariDataSource

    fun connect() {
        neon.logger.info(appContext.getCodeMessage("neon.info.database.connecting_to_db_server"))

        runCatching {
            dataSource = dataSourceHandler.getHikariDataSource()
        }.onFailure {
            it.printStackTrace()
        }.onSuccess {
            neon.logger.info(appContext.getCodeMessage("neon.info.database.db_connection_success"))
        }
    }

    fun disconnect() {
        neon.logger.info(appContext.getCodeMessage("neon.info.database.disconnecting_from_db_server"))

        runCatching {
            dataSource.close()
        }.onFailure {
            it.printStackTrace()
        }.onSuccess {
            neon.logger.info(appContext.getCodeMessage("neon.info.database.db_disconnection_success"))
        }

    }

    fun getDataSourceConfig(): HikariConfig = dataSourceHandler.getHikariConfig()

    @JvmName("getHikariDataSource")
    fun getDataSource(): HikariDataSource = dataSource
}