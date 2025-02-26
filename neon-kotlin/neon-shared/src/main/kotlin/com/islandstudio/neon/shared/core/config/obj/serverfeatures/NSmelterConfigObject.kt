package com.islandstudio.neon.shared.core.config.obj.serverfeatures

import com.islandstudio.neon.shared.core.config.component.type.IConfigObject
import com.islandstudio.neon.shared.core.config.property.NeonServerFeaturesConfigProperty
import kotlinx.serialization.Serializable

@Serializable
data class NSmelterConfigObject(
    var isEnabled: Boolean = NeonServerFeaturesConfigProperty.NSmelterConfigProperty.IsEnabled.defaultValue
): IConfigObject