package com.islandstudio.neon.stable.core.config.component

import com.islandstudio.neon.stable.core.config.component.type.IConfigProperty
import com.islandstudio.neon.stable.utils.processing.properties.DataTypes

open class ConfigProperty(
    val parentConfigKey: String = "rootNode",
    val keyName: String,
    val description: String,
    val dataType: DataTypes,
    val defaultValue: Any,
    val dataRange: ConfigDataRange<*>
): IConfigProperty