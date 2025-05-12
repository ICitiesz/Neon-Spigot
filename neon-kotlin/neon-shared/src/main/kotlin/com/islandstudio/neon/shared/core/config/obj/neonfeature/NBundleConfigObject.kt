package com.islandstudio.neon.shared.core.config.obj.neonfeature

import com.islandstudio.neon.shared.core.config.component.type.IConfigObject
import com.islandstudio.neon.shared.core.config.property.NeonFeatureConfigProperty
import kotlinx.serialization.Serializable

@Serializable
data class NBundleConfigObject(
    var isEnabled: Boolean = NeonFeatureConfigProperty.NBundleConfigProperty.IsEnabled.defaultValue,
    var options: NBundleConfigOptions = NBundleConfigOptions()
): IConfigObject {
    @Serializable
    data class NBundleConfigOptions(
        var bundleGenerateChance: Double = NeonFeatureConfigProperty.NBundleConfigProperty.BundleGenerateChance.defaultValue,
        var bundleMaxBuy: Long = NeonFeatureConfigProperty.NBundleConfigProperty.BundleMaxBuy.defaultValue,
        var bundlePrice: Long = NeonFeatureConfigProperty.NBundleConfigProperty.BundlePrice.defaultValue,
        var bundlePriceMultiplier: Double = NeonFeatureConfigProperty.NBundleConfigProperty.BundlePriceMultiplier.defaultValue,
        var villagerExperience: Long = NeonFeatureConfigProperty.NBundleConfigProperty.VilagerExperience.defaultValue
    ): IConfigObject
}
