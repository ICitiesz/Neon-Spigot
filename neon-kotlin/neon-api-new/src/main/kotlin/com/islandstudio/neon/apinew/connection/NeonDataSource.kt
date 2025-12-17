package com.islandstudio.neon.apinew.connection

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

class NeonDataSource private constructor(hikariConfig: HikariConfig): HikariDataSource(hikariConfig) {
    companion object {
        fun create(url: String, dbUsername: String, dbPassword: String = ""): NeonDataSource {
//            val hikariConfig = HikariConfig().apply {
//                jdbcUrl = "jdbc:sqlite:${NeonDataFolderNew.NeonDatabaseFolder.absolutePath}${File.separatorChar}Neon_Online-preview.db"
//                driverClassName = "org.sqlite.JDBC"
//                this.maximumPoolSize = 5
//                this.poolName = "Neon Database Pool"
//                this.isAutoCommit = true
//            }

            val hikariConfig = HikariConfig().apply {
                jdbcUrl = url // "jdbc:mariadb://localhost:3306/neon_online"
                username = dbUsername
                password = dbPassword
                driverClassName = "org.mariadb.jdbc.Driver"
                this.maximumPoolSize = 5
                this.poolName = "Neon Database Pool"
                this.isAutoCommit = true
            }

            return NeonDataSource(hikariConfig)
        }
    }
}