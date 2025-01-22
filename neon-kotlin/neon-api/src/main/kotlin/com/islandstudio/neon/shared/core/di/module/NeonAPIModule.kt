package com.islandstudio.neon.shared.core.di.module

import com.islandstudio.neon.ext.neondatabaseserver.NeonDatabaseServer
import org.bukkit.plugin.java.JavaPlugin.getPlugin
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import java.sql.Connection

@Module
@ComponentScan("com.islandstudio.neon.api")
class NeonAPIModule {
    @Single
    fun provideDSLContext(): DSLContext {
        val neonDatabaseServer = getPlugin(NeonDatabaseServer::class.java)

        return DSL.using(neonDatabaseServer.getConnectionManager().getDataSource(), SQLDialect.HSQLDB)
    }

    @Single
    fun provideConnection(): Connection {
        val neonDatabaseServer = getPlugin(NeonDatabaseServer::class.java)

        return neonDatabaseServer.getConnectionManager().getDataSource().connection
    }
}