package com.islandstudio.neon.shared.core.config.obj.neonfeature

import com.islandstudio.neon.shared.core.config.component.type.IConfigObject
import com.islandstudio.neon.shared.core.config.property.NeonFeatureConfigProperty
import kotlinx.serialization.Serializable

@Serializable
data class DurabilityPlusConfigObject(
    var isEnabled: Boolean = NeonFeatureConfigProperty.DurabilityPlusConfigProperty.IsEnabled.defaultValue,
    var options: DurabilityPlusConfigOptions = DurabilityPlusConfigOptions()
): IConfigObject {
    @Serializable
    data class DurabilityPlusConfigOptions(
        var showItemDurability: Boolean = NeonFeatureConfigProperty.DurabilityPlusConfigProperty.ShowItemDurability.defaultValue
    ): IConfigObject
}
