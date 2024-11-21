package com.islandstudio.neondatabaseserver.application.di.module

import com.islandstudio.neondatabaseserver.NeonDatabaseServer
import org.bukkit.plugin.java.JavaPlugin.getPlugin
import org.hsqldb.server.Server
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.islandstudio.neon.stable.core.database")
class GeneralModule {

    @Single
    fun provideHsqldbServer(): Server {
        return Server()
    }

    @Single
    fun provideNeonDatabaseServer(): NeonDatabaseServer {
        return getPlugin(NeonDatabaseServer::class.java)
    }
}