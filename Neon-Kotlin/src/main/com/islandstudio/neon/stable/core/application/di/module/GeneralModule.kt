package com.islandstudio.neon.stable.core.application.di.module

import com.islandstudio.neon.Neon
import com.islandstudio.neon.stable.core.application.server.ServerRunningMode
import org.bukkit.plugin.java.JavaPlugin.getPlugin
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.islandstudio.neon")
class GeneralModule {
    @Single
    fun provideNeonPlugin(): Neon {
        return getPlugin(Neon::class.java)
    }

    @Single
    fun provideServerRunningMode(neon: Neon): ServerRunningMode {
        if (neon.server.onlineMode) {
            return ServerRunningMode.ONLINE
        }

        return ServerRunningMode.OFFLINE
    }
}