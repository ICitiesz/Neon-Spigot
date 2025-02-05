package com.islandstudio.neon.shared.utils.data

import com.islandstudio.neon.shared.core.di.IComponentInjector
import org.koin.core.component.inject
import org.modelmapper.ModelMapper

object ObjectMapper: IComponentInjector {
    private val modelMapper by inject<ModelMapper>()

    fun getMapper(): ModelMapper {
        return modelMapper
    }

    fun <T, U> mapTo(source: T, resultClazz: Class<U>): U {
        return modelMapper.map(source, resultClazz)
    }
}