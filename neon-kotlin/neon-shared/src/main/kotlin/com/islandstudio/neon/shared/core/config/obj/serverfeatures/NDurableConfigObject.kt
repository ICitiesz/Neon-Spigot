package com.islandstudio.neon.shared.core.config.obj.serverfeatures

import com.islandstudio.neon.shared.core.config.component.type.IConfigObject
import com.islandstudio.neon.shared.core.config.property.NeonServerFeaturesConfigProperty
import kotlinx.serialization.Serializable

@Serializable
data class NDurableConfigObject(
    var isEnabled: Boolean = NeonServerFeaturesConfigProperty.NDurableConfigProperty.IsEnabled.defaultValue,
    var options: NDurableConfigOptions = NDurableConfigOptions()
): IConfigObject {
    @Serializable
    data class NDurableConfigOptions(
        var showItemDurability: Boolean = NeonServerFeaturesConfigProperty.NDurableConfigProperty.ShowItemDurability.defaultValue
    ): IConfigObject
}
