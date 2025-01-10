package com.islandstudio.neon.shared.core.config.component

import com.islandstudio.neon.shared.core.config.component.type.IConfigProperty
import com.islandstudio.neon.shared.utils.data.DataType

open class ConfigProperty(
    val parentConfigKey: String = "rootNode",
    val keyName: String,
    val description: String,
    val dataType: DataType,
    val defaultValue: Any,
    val dataRange: ConfigDataRange<*>
): IConfigProperty