package com.islandstudio.neon.shared.core.config

import com.islandstudio.neon.shared.core.config.component.type.IConfigObject
import com.islandstudio.neon.shared.core.config.component.type.IConfigProperty
import com.islandstudio.neon.shared.utils.serialization.ObjectSerializer
import kotlin.reflect.KClass

class ConfigWrapper<T: IConfigObject, U: IConfigProperty>(
    private val configPropertyClazz: KClass<U>
) {
    private lateinit var immutableConfigObject: T
    private lateinit var mutableConfigObject: T

    fun initConfigObject(configObject: T) {
        immutableConfigObject = configObject
        mutableConfigObject = configObject
    }

    fun updateMutableConfigObject(configObject: T) {
        mutableConfigObject = configObject
    }

    fun getImmutableConfigObject(): T {
        return immutableConfigObject
    }

    fun getMutableConfigObject(): T {
        return mutableConfigObject
    }

    fun copyMutableConfigObject(): T {
        val serializedMutableConfigObject = ObjectSerializer.serializeToByteArray(mutableConfigObject)

        return ObjectSerializer.deserialzeFromByteArray(serializedMutableConfigObject)
    }

    fun getAllConfigProperty(): ArrayList<U> {
        return configPropertyClazz
            .sealedSubclasses
            .filter { it.objectInstance != null }
            .map {
                it.objectInstance as U
            }
            .toCollection(ArrayList())
    }
}