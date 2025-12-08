package com.islandstudio.neon.core.di.module

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module
@ComponentScan("com.islandstudio.neon")
class NeonModule {

//    @Single([JavaPlugin::class])
//    fun provideNeon(): Neon {
//        return getPlugin(Neon::class.java)
//    }
}