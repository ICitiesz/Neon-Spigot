package com.islandstudio.neon.shared.utils.data

import org.koin.core.annotation.Single
import org.modelmapper.ModelMapper

@Single
class ModelMapperUtil: ModelMapper() {
    fun <T, U> mapTo(source: T, resultClazz: Class<U>): U {
        return map(source, resultClazz)
    }
}