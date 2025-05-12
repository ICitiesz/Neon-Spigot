package com.islandstudio.neon.shared.core.config.obj.neonfeature

import com.islandstudio.neon.shared.core.config.component.type.IConfigObject
import com.islandstudio.neon.shared.core.config.property.NeonFeatureConfigProperty
import kotlinx.serialization.Serializable

@Serializable
data class NPaintingConfigObject(
    var isEnabled: Boolean = NeonFeatureConfigProperty.NPaintingConfigProperty.IsEnabled.defaultValue
): IConfigObject
