package com.islandstudio.neon.persistence

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

class DataSource private constructor(hikariConfig: HikariConfig): HikariDataSource(hikariConfig) {
    companion object {
        fun create(url: String, dbUsername: String, dbPassword: String = ""): DataSource {
            val hikariConfig = HikariConfig().apply {
                jdbcUrl = url
                username = dbUsername
                password = dbPassword
                driverClassName = "org.mariadb.jdbc.Driver"
                this.maximumPoolSize = 5
                this.poolName = "Neon Database Pool"
                this.isAutoCommit = true
            }

            return DataSource(hikariConfig)
        }
    }
}