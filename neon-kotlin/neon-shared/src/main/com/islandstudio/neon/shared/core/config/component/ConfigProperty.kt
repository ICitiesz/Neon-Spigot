package com.islandstudio.neon.shared.core.config.component

import com.islandstudio.neon.shared.core.config.component.type.IConfigProperty

open class ConfigProperty(
    val parentConfigKey: String = "rootNode",
    val keyName: String,
    val description: String,
    val dataType: Any,
    val defaultValue: Any,
    val dataRange: ConfigDataRange<*>
): IConfigProperty