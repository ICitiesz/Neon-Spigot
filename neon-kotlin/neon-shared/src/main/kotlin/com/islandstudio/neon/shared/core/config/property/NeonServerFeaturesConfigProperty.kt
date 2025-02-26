package com.islandstudio.neon.shared.core.config.property

import com.islandstudio.neon.shared.core.config.component.AbstractConfigProperty
import com.islandstudio.neon.shared.core.config.component.ConfigDataRange
import com.islandstudio.neon.shared.utils.data.DataType

sealed class NeonServerFeaturesConfigProperty<T>: AbstractConfigProperty<T>() {
    private interface INeonServerFeaturesConfigDetail {
        val serverFeatureDescription: String
    }

    object NHarvestConfigProperty: INeonServerFeaturesConfigDetail {
        override val serverFeatureDescription: String = "Makes harvesting crops much easier!"

        data object IsEnabled: NeonServerFeaturesConfigProperty<Boolean>() {
            override val parentConfigKey: String = "nHarvest"
            override val keyName: String = "isEnabled"
            override val description: String = "Toggle status."
            override val dataType: DataType = DataType.Boolean
            override val defaultValue: Boolean = false
            override val dataRange: ConfigDataRange<Boolean> = ConfigDataRange.DataRangeBoolean
        }
    }

    object NCutterConfigProperty: INeonServerFeaturesConfigDetail {
        override val serverFeatureDescription: String = "Enables stonecutter to craft some of the woord items with fewer ingredients."

        data object IsEnabled: NeonServerFeaturesConfigProperty<Boolean>() {
            override val parentConfigKey: String = "nCutter"
            override val keyName: String = "isEnabled"
            override val description: String = "Toggle status."
            override val dataType: DataType = DataType.Boolean
            override val defaultValue: Boolean = false
            override val dataRange: ConfigDataRange<Boolean> = ConfigDataRange.DataRangeBoolean
        }
    }

    object NSmelterConfigProperty: INeonServerFeaturesConfigDetail {
        override val serverFeatureDescription: String = "Enables blast furnace to smelft some of the " +
                "smeltable items that only the furnace can smelt."

        data object IsEnabled: NeonServerFeaturesConfigProperty<Boolean>() {
            override val parentConfigKey: String = "nSmelter"
            override val keyName: String = "isEnabled"
            override val description: String = "Toggle status."
            override val dataType: DataType = DataType.Boolean
            override val defaultValue: Boolean = false
            override val dataRange: ConfigDataRange<Boolean> = ConfigDataRange.DataRangeBoolean
        }
    }

    object NPVPConfigProperty: INeonServerFeaturesConfigDetail {
        override val serverFeatureDescription: String = "Enables or disables PvP in the server."

        data object IsEnabled: NeonServerFeaturesConfigProperty<Boolean>() {
            override val parentConfigKey: String = "nPVP"
            override val keyName: String = "isEnabled"
            override val description: String = "Toggle status."
            override val dataType: DataType = DataType.Boolean
            override val defaultValue: Boolean = false
            override val dataRange: ConfigDataRange<Boolean> = ConfigDataRange.DataRangeBoolean
        }
    }

    object NWaypointsConfigProperty: INeonServerFeaturesConfigDetail {
        override val serverFeatureDescription: String = "Enables to save waypoints, " +
                "remove waypoints and teleport to waypoint in the server."

        data object IsEnabled: NeonServerFeaturesConfigProperty<Boolean>() {
            override val parentConfigKey: String = "nWaypoints"
            override val keyName: String = "isEnabled"
            override val description: String = "Toggle status."
            override val dataType: DataType = DataType.Boolean
            override val defaultValue: Boolean = false
            override val dataRange: ConfigDataRange<Boolean> = ConfigDataRange.DataRangeBoolean
        }

        data object CrossDimension: NeonServerFeaturesConfigProperty<Boolean>() {
            override val parentConfigKey: String = "nWaypoints.options"
            override val keyName: String = "crossDimension"
            override val description: String = "Enable cross dimension teleportation."
            override val dataType: DataType = DataType.Boolean
            override val defaultValue: Boolean = false
            override val dataRange: ConfigDataRange<Boolean> = ConfigDataRange.DataRangeBoolean
        }
    }

    object NDurableConfigProperty: INeonServerFeaturesConfigDetail {
        override val serverFeatureDescription: String = "Another way of handling the " +
                "durability of the tools/weapons."

        data object IsEnabled: NeonServerFeaturesConfigProperty<Boolean>() {
            override val parentConfigKey: String = "nDurable"
            override val keyName: String = "isEnabled"
            override val description: String = "Toggle status."
            override val dataType: DataType = DataType.Boolean
            override val defaultValue: Boolean = false
            override val dataRange: ConfigDataRange<Boolean> = ConfigDataRange.DataRangeBoolean
        }

        data object ShowItemDurability: NeonServerFeaturesConfigProperty<Boolean>() {
            override val parentConfigKey: String = "nDurable.options"
            override val keyName: String = "showItemDurability"
            override val description: String = "Show/hide item durability."
            override val dataType: DataType = DataType.Boolean
            override val defaultValue: Boolean = true
            override val dataRange: ConfigDataRange<Boolean> = ConfigDataRange.DataRangeBoolean
        }
    }

    object NBundleConfigProperty: INeonServerFeaturesConfigDetail {
        override val serverFeatureDescription: String = "Since the bundle is unobtainable in survival mode within " +
                "Minecraft 1.17 ~ 1.20, it is now obtainable in survival mode."

        data object IsEnabled: NeonServerFeaturesConfigProperty<Boolean>() {
            override val parentConfigKey: String = "nBundle"
            override val keyName: String = "isEnabled"
            override val description: String = "Toggle status."
            override val dataType: DataType = DataType.Boolean
            override val defaultValue: Boolean = false
            override val dataRange: ConfigDataRange<Boolean> = ConfigDataRange.DataRangeBoolean
        }

        data object BundleGenerateChance: NeonServerFeaturesConfigProperty<Double>() {
            override val parentConfigKey: String = "nBundle.options"
            override val keyName: String = "bundleGenerateChance"
            override val description: String = "How frequent Bundle can generate in loot chests."
            override val dataType: DataType = DataType.Double
            override val defaultValue: Double = 0.17
            override val dataRange: ConfigDataRange<Double> = ConfigDataRange(0.001, 1.0)
        }

        data object BundleMaxBuy: NeonServerFeaturesConfigProperty<Long>() {
            override val parentConfigKey: String = "nBundle.options"
            override val keyName: String = "bundleMaxBuy"
            override val description: String = "How many Bundle you can buy from Tannery Villager per stock."
            override val dataType: DataType = DataType.Long
            override val defaultValue: Long = 12
            override val dataRange: ConfigDataRange<Long> = ConfigDataRange(1, 64)
        }

        data object BundlePrice: NeonServerFeaturesConfigProperty<Long>() {
            override val parentConfigKey: String = "nBundle.options"
            override val keyName: String = "bundlePrice"
            override val description: String = "How much (emeralds) per Bundle."
            override val dataType: DataType = DataType.Long
            override val defaultValue: Long = 5
            override val dataRange: ConfigDataRange<Long> = ConfigDataRange(1, 64)
        }

        data object BundlePriceMultiplier: NeonServerFeaturesConfigProperty<Double>() {
            override val parentConfigKey: String = "nBundle.options"
            override val keyName: String = "bundlePriceMultiplier"
            override val description: String = "How much price will be multiplied when Bundle in demand."
            override val dataType: DataType = DataType.Double
            override val defaultValue: Double = 0.2
            override val dataRange: ConfigDataRange<Double> = ConfigDataRange(0.1, 1.0)
        }

        data object VilagerExperience: NeonServerFeaturesConfigProperty<Long>() {
            override val parentConfigKey: String = "nBundle.options"
            override val keyName: String = "villagerExperience"
            override val description: String = "How much xp will villager get per trade."
            override val dataType: DataType = DataType.Long
            override val defaultValue: Long = 5
            override val dataRange: ConfigDataRange<Long> = ConfigDataRange(1, 64)
        }
    }

    object NFireworksConfigProperty: INeonServerFeaturesConfigDetail {
        override val serverFeatureDescription: String = "[Experimental] Create custom firework pattern " +
                "by using imported images."

        data object IsEnabled: NeonServerFeaturesConfigProperty<Boolean>() {
            override val parentConfigKey: String = "nFireworks"
            override val keyName: String = "isEnabled"
            override val description: String = "Toggle status."
            override val dataType: DataType = DataType.Boolean
            override val defaultValue: Boolean = false
            override val dataRange: ConfigDataRange<Boolean> = ConfigDataRange.DataRangeBoolean
        }

        data object ParticleSize: NeonServerFeaturesConfigProperty<Double>() {
            override val parentConfigKey: String = "nFireworks.options"
            override val keyName: String = "particleSize"
            override val description: String = ""
            override val dataType: DataType = DataType.Double
            override val defaultValue: Double = 1.0
            override val dataRange: ConfigDataRange<Double> = ConfigDataRange(0.1, 1.0)
        }

        data object ParticleSpeed: NeonServerFeaturesConfigProperty<Double>() {
            override val parentConfigKey: String = "nFireworks.options"
            override val keyName: String = "particleSpeed"
            override val description: String = ""
            override val dataType: DataType = DataType.Double
            override val defaultValue: Double = 1.0
            override val dataRange: ConfigDataRange<Double> = ConfigDataRange(0.1, 1.0)
        }
    }

    object NPaintingConfigProperty: INeonServerFeaturesConfigDetail {
        override val serverFeatureDescription: String = "[Experimental] (Incompatible with Minecraft 1.17.X) " +
                "Create custom painting by using imported images."

        data object IsEnabled: NeonServerFeaturesConfigProperty<Boolean>() {
            override val parentConfigKey: String = "nPainting"
            override val keyName: String = "isEnabled"
            override val description: String = "Toggle status."
            override val dataType: DataType = DataType.Boolean
            override val defaultValue: Boolean = false
            override val dataRange: ConfigDataRange<Boolean> = ConfigDataRange.DataRangeBoolean
        }
    }
}