package com.islandstudio.neon.shared.rework.core.config.component

import com.islandstudio.neon.shared.utils.data.DataType

abstract class AbstractConfigEntry<T>(val entryKey: String): ConfigDescriptor {
    abstract val defaultValue: T
    abstract val dataType: DataType
    abstract val dataRange: ConfigDataRange<T>
}