package com.islandstudio.neon.shared.core.config.component.type

import kotlin.reflect.KClass

abstract class AbstractConfigWrapper<T: IConfigObject, U: IConfigProperty>(
    private val configObjectInstance: T,
    private val configPropertyClass: KClass<U>
) {
//    private lateinit var immutableConfigObject: T
//    private lateinit var mutableConfigObject: T
//
//    fun initConfigObject(configObject: T) {
//        immutableConfigObject = configObject
//        mutableConfigObject = configObject
//    }
//
//    fun updateMutableConfigObject(configObject: T) {
//        mutableConfigObject = configObject
//    }
//
//    fun getImmutableConfigObject(): T {
//        return immutableConfigObject
//    }
//
//    fun getMutableConfigObject(): T {
//        return mutableConfigObject
//    }
//
//    fun copyMutableConfigObject(): T {
//        val serializedMutableConfigObject = ObjectSerializer.serializeObjectRaw(mutableConfigObject)
//
//        return ObjectSerializer.deserializeObjectRaw(serializedMutableConfigObject) as T
//    }
//
//    fun getDefaultConfigObject(): T {
//        return configObjectInstance::class.createInstance()
//    }
//
//    fun getAllConfigProperty(): ArrayList<ConfigProperty> {
//        return configPropertyClass
//            .sealedSubclasses
//            .map {
//                it.objectInstance as ConfigProperty
//            }
//            .toCollection(ArrayList())
//    }
}