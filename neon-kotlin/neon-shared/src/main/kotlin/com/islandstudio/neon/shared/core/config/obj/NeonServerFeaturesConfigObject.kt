package com.islandstudio.neon.shared.core.config.obj

import com.islandstudio.neon.shared.core.config.component.type.IConfigObject
import com.islandstudio.neon.shared.core.config.property.NeonServerFeaturesConfigProperty
import kotlinx.serialization.Serializable

@Serializable
data class NeonServerFeaturesConfigObject(
    var nHarvest: NHarvestConfigObject = NHarvestConfigObject(),
    var nCutter: NCutterConfigObject = NCutterConfigObject(),
    var nSmelter: NSmelterConfigObject = NSmelterConfigObject(),
    var nPVP: NPVPConfigObject = NPVPConfigObject(),
    var nWaypoints: NWaypointsConfigObject = NWaypointsConfigObject(),
    var nDurable: NDurableConfigObject = NDurableConfigObject(),
    var nBundle: NBundleConfigObject = NBundleConfigObject(),
    var nFireworks: NFireworksConfigObject = NFireworksConfigObject(),
    var nPainting: NPaintingConfigObject = NPaintingConfigObject()
): IConfigObject {
    @Serializable
    data class NHarvestConfigObject(
        var isEnabled: Boolean = NeonServerFeaturesConfigProperty.NHarvestConfigProperty.IsEnabled.defaultValue
    ): IConfigObject

    @Serializable
    data class NCutterConfigObject(
        var isEnabled: Boolean = NeonServerFeaturesConfigProperty.NCutterConfigProperty.IsEnabled.defaultValue
    ): IConfigObject

    @Serializable
    data class NSmelterConfigObject(
        var isEnabled: Boolean = NeonServerFeaturesConfigProperty.NSmelterConfigProperty.IsEnabled.defaultValue
    ): IConfigObject

    @Serializable
    data class NPVPConfigObject(
        var isEnabled: Boolean = NeonServerFeaturesConfigProperty.NPVPConfigProperty.IsEnabled.defaultValue
    ): IConfigObject

    @Serializable
    data class NWaypointsConfigObject(
        var isEnabled: Boolean = NeonServerFeaturesConfigProperty.NWaypointsConfigProperty.IsEnabled.defaultValue,
        var options: NWaypointsConfigOptions = NWaypointsConfigOptions()
    ) {
        @Serializable
        data class NWaypointsConfigOptions(
            var crossDimension: Boolean = NeonServerFeaturesConfigProperty.NWaypointsConfigProperty.CrossDimension.defaultValue
        ): IConfigObject
    }

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

    @Serializable
    data class NPaintingConfigObject(
        var isEnabled: Boolean = NeonServerFeaturesConfigProperty.NPaintingConfigProperty.IsEnabled.defaultValue
    ): IConfigObject
}