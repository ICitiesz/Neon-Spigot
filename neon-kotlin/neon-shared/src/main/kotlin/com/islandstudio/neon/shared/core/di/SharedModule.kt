package com.islandstudio.neon.shared.core.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.modelmapper.ModelMapper

@Module
@ComponentScan("com.islandstudio.neon.shared")
class SharedModule {
    @Single
    fun provideObjectMapper(): ModelMapper {
        return ModelMapper()
    }
}