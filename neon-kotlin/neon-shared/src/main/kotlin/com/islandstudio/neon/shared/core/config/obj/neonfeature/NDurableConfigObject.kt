package com.islandstudio.neon.shared.core.config.obj.neonfeature

import com.islandstudio.neon.shared.core.config.component.type.IConfigObject
import com.islandstudio.neon.shared.core.config.property.NeonFeatureConfigProperty
import kotlinx.serialization.Serializable

@Serializable
data class NDurableConfigObject(
    var isEnabled: Boolean = NeonFeatureConfigProperty.NDurableConfigProperty.IsEnabled.defaultValue,
    var options: NDurableConfigOptions = NDurableConfigOptions()
): IConfigObject {
    @Serializable
    data class NDurableConfigOptions(
        var showItemDurability: Boolean = NeonFeatureConfigProperty.NDurableConfigProperty.ShowItemDurability.defaultValue
    ): IConfigObject
}
