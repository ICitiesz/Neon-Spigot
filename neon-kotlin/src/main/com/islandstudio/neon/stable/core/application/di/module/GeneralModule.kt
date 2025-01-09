package com.islandstudio.neon.stable.core.application.di.module

import com.islandstudio.neon.Neon
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.JavaPlugin.getPlugin
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.islandstudio.neon")
class GeneralModule {

    @Single([JavaPlugin::class])
    fun provideNeon(): Neon {
        return getPlugin(Neon::class.java)
    }
}