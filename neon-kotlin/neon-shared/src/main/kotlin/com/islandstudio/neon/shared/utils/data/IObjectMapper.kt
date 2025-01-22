package com.islandstudio.neon.shared.utils.data

import com.islandstudio.neon.shared.core.di.IComponentInjector
import org.koin.core.component.inject
import org.modelmapper.ModelMapper

interface IObjectMapper: IComponentInjector {
    fun objectMapper(): ModelMapper {
        val objectMapper by inject<ModelMapper>()

        return objectMapper
    }

    fun <T> mapTo(source: Any, resultType: Class<T>): T {
        return objectMapper().map(source, resultType)
    }
}