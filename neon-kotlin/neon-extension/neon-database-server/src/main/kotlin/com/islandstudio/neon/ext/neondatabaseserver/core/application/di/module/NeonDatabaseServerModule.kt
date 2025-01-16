package com.islandstudio.neon.ext.neondatabaseserver.core.application.di.module

import com.islandstudio.neon.ext.neondatabaseserver.NeonDatabaseServer
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.JavaPlugin.getPlugin
import org.hsqldb.server.Server
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.islandstudio.neon")
class NeonDatabaseServerModule {
    @Single
    fun provideHsqldbServer(): Server {
        return Server()
    }

    @Single([JavaPlugin::class])
    fun provideNeonDatabaseServer(): NeonDatabaseServer {
        return getPlugin(NeonDatabaseServer::class.java)
    }
}