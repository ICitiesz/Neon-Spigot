package com.islandstudio.neon.shared.rework.core.config.component

import com.islandstudio.neon.shared.utils.data.DataType
import com.islandstudio.neon.shared.utils.data.DataUtil

sealed class ConfigDataRange<T> {
    companion object Validator {
        fun validate(value: Any, dataType: DataType, dataRange: ConfigDataRange<*>): Boolean {
            return when(dataRange) {
                is ByMinMax<*> -> {
                    DataUtil.validateDataRange(value, dataType, dataRange.min, dataRange.max)
                }

                is BySelection<*> -> {
                    dataRange.selection.contains(value)
                }

                is ByBoolean -> {
                    DataUtil.validateDataRange(value, dataType, ByBoolean.TRUE, ByBoolean.FALSE)
                }
            }
        }
    }

    data class ByMinMax<T>(val min: T, val max: T): ConfigDataRange<T>()

    data object ByBoolean: ConfigDataRange<Boolean>() {
        const val TRUE = true
        const val FALSE = false
    }

    data class BySelection<T>(val selection: Array<T>): ConfigDataRange<T>() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as BySelection<*>

            return selection.contentEquals(other.selection)
        }

        override fun hashCode(): Int {
            return selection.contentHashCode()
        }

    }
}