package com.islandstudio.neon.shared.core.config.obj.neonfeature

import com.islandstudio.neon.shared.core.config.component.type.IConfigObject
import com.islandstudio.neon.shared.core.config.property.NeonFeatureConfigProperty
import kotlinx.serialization.Serializable

@Serializable
data class NFireworksConfigObject(
    var isEnabled: Boolean = NeonFeatureConfigProperty.NFireworksConfigProperty.IsEnabled.defaultValue,
    var options: NFireworksConfigOptions = NFireworksConfigOptions()
): IConfigObject {
    @Serializable
    data class NFireworksConfigOptions(
        var particleSize: Double = NeonFeatureConfigProperty.NFireworksConfigProperty.ParticleSize.defaultValue,
        var particleSpeed: Double = NeonFeatureConfigProperty.NFireworksConfigProperty.ParticleSpeed.defaultValue
    ): IConfigObject
}
