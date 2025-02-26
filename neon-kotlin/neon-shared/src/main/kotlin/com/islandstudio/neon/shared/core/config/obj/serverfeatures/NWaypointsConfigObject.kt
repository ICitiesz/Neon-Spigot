package com.islandstudio.neon.shared.core.config.obj.serverfeatures

import com.islandstudio.neon.shared.core.config.component.type.IConfigObject
import com.islandstudio.neon.shared.core.config.property.NeonServerFeaturesConfigProperty
import kotlinx.serialization.Serializable

@Serializable
data class NWaypointsConfigObject(
    var isEnabled: Boolean = NeonServerFeaturesConfigProperty.NWaypointsConfigProperty.IsEnabled.defaultValue,
    var options: NWaypointsConfigOptions = NWaypointsConfigOptions()
): IConfigObject {
    @Serializable
    data class NWaypointsConfigOptions(
        var crossDimension: Boolean = NeonServerFeaturesConfigProperty.NWaypointsConfigProperty.CrossDimension.defaultValue
    ): IConfigObject
}
