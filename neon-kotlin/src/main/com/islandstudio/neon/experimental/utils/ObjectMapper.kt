package com.islandstudio.neon.experimental.utils

import org.modelmapper.ModelMapper

object ObjectMapper {
    private val modelMapper = ModelMapper()

    fun <T> mapToModel(obj: Any, modelClazz: Class<T>): T {
        return modelMapper.map(obj, modelClazz)
    }

    fun <T> mapToModelList(objList: List<Any>, modelClazz: Class<T>): List<T> {
        return ArrayList<T>().apply {
            objList.forEach {
                this.add(mapToModel(it, modelClazz))
            }
        }
    }
}