package com.islandstudio.neon.shared.core.config.obj.serverfeatures

import com.islandstudio.neon.shared.core.config.component.type.IConfigObject
import com.islandstudio.neon.shared.core.config.property.NeonServerFeaturesConfigProperty
import kotlinx.serialization.Serializable

@Serializable
data class NFireworksConfigObject(
    var isEnabled: Boolean = NeonServerFeaturesConfigProperty.NFireworksConfigProperty.IsEnabled.defaultValue,
    var options: NFireworksConfigOptions = NFireworksConfigOptions()
): IConfigObject {
    @Serializable
    data class NFireworksConfigOptions(
        var particleSize: Double = NeonServerFeaturesConfigProperty.NFireworksConfigProperty.ParticleSize.defaultValue,
        var particleSpeed: Double = NeonServerFeaturesConfigProperty.NFireworksConfigProperty.ParticleSpeed.defaultValue
    ): IConfigObject
}
