package com.islandstudio.neon.shared.core.config.obj.serverfeatures

import com.islandstudio.neon.shared.core.config.component.type.IConfigObject
import com.islandstudio.neon.shared.core.config.property.NeonServerFeaturesConfigProperty
import kotlinx.serialization.Serializable

@Serializable
data class NBundleConfigObject(
    var isEnabled: Boolean = NeonServerFeaturesConfigProperty.NBundleConfigProperty.IsEnabled.defaultValue,
    var options: NBundleConfigOptions = NBundleConfigOptions()
): IConfigObject {
    @Serializable
    data class NBundleConfigOptions(
        var bundleGenerateChance: Double = NeonServerFeaturesConfigProperty.NBundleConfigProperty.BundleGenerateChance.defaultValue,
        var bundleMaxBuy: Long = NeonServerFeaturesConfigProperty.NBundleConfigProperty.BundleMaxBuy.defaultValue,
        var bundlePrice: Long = NeonServerFeaturesConfigProperty.NBundleConfigProperty.BundlePrice.defaultValue,
        var bundlePriceMultiplier: Double = NeonServerFeaturesConfigProperty.NBundleConfigProperty.BundlePriceMultiplier.defaultValue,
        var villagerExperience: Long = NeonServerFeaturesConfigProperty.NBundleConfigProperty.VilagerExperience.defaultValue
    ): IConfigObject
}
