package com.islandstudio.neon.shared.rework.core.di.module

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.modelmapper.ModelMapper

@Module
@ComponentScan("com.islandstudio.neon.shared")
class SharedComponentModule {
    @Single
    fun provideObjectMapper(): ModelMapper {
        return ModelMapper()
    }
}