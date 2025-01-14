package com.islandstudio.neon.application.di.module

import com.islandstudio.neon.NeonDatabaseServer
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.JavaPlugin.getPlugin
import org.hsqldb.server.Server
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.islandstudio.neon")
class GeneralModule {

    @Single
    fun provideHsqldbServer(): Server {
        return Server()
    }

    @Single([JavaPlugin::class])
    fun provideNeonDatabaseServer(): NeonDatabaseServer {
        return getPlugin(NeonDatabaseServer::class.java)
    }
//    @Single
//    fun provideDSLConfiguration(): Configuration {
//        val dsl = DSL.using(DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/neondatabase", "SA", ""), SQLDialect.HSQLDB)
//
//        return dsl.configuration()
//    }
}