package com.islandstudio.neon.shared.core.config.component

import com.islandstudio.neon.shared.core.config.component.type.IConfigProperty
import com.islandstudio.neon.shared.utils.data.DataType

abstract class AbstractConfigProperty<T>: IConfigProperty {
    open val parentConfigKey: String = "rootNode"
    abstract val keyName: String
    abstract val description: String
    abstract val dataType: DataType
    abstract val defaultValue: T
    abstract val dataRange: ConfigDataRange<T>

    open fun <T: IConfigProperty>getConfigCustomData(): IConfigCustomData<T>? {
        return null
    }

    interface IConfigCustomData<T: IConfigProperty> {
        val customData: T
    }
}