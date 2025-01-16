package com.islandstudio.neon.stable.utils

object NValidator {
    fun validateNull(obj: Any?): Boolean {
        return obj == null
    }

    fun validateMaxValue(value: Long, maxValue: Long): Boolean {
        return value > maxValue
    }

    fun validateCollectionIsEmpty(collection: Collection<*>): Boolean {
        return collection.isEmpty()
    }
}