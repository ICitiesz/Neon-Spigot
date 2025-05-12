package com.islandstudio.neon.shared.core.config.obj.neonfeature

import com.islandstudio.neon.shared.core.config.component.type.IConfigObject
import com.islandstudio.neon.shared.core.config.property.NeonFeatureConfigProperty
import kotlinx.serialization.Serializable

@Serializable
data class NWaypointsConfigObject(
    var isEnabled: Boolean = NeonFeatureConfigProperty.NWaypointsConfigProperty.IsEnabled.defaultValue,
    var options: NWaypointsConfigOptions = NWaypointsConfigOptions()
): IConfigObject {
    @Serializable
    data class NWaypointsConfigOptions(
        var crossDimension: Boolean = NeonFeatureConfigProperty.NWaypointsConfigProperty.CrossDimension.defaultValue
    ): IConfigObject
}
