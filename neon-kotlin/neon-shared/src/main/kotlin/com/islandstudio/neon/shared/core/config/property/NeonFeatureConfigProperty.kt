package com.islandstudio.neon.shared.core.config.property

import com.islandstudio.neon.shared.core.config.component.AbstractConfigProperty
import com.islandstudio.neon.shared.core.config.component.ConfigDataRange
import com.islandstudio.neon.shared.core.config.component.type.IConfigProperty
import com.islandstudio.neon.shared.core.config.property.custom.NeonFeatureConfigCustomData
import com.islandstudio.neon.shared.utils.data.DataType

sealed class NeonFeatureConfigProperty<T>: AbstractConfigProperty<T>() {
    object NHarvestConfigProperty: IConfigCustomData<NeonFeatureConfigCustomData> {
        override val customData: NeonFeatureConfigCustomData = NeonFeatureConfigCustomData(
            "Makes harvesting crops much easier!"
        )

        data object IsEnabled: NeonFeatureConfigProperty<Boolean>() {
            override val parentConfigKey: String = "nHarvest"
            override val keyName: String = "isEnabled"
            override val description: String = "Toggle status."
            override val dataType: DataType = DataType.Boolean
            override val defaultValue: Boolean = false
            override val dataRange: ConfigDataRange<Boolean> = ConfigDataRange.DataRangeBoolean

            override fun <T: IConfigProperty> getConfigCustomData(): T? {
                return this@NHarvestConfigProperty.customData as T?
            }
        }
    }

    object NCutterConfigProperty: IConfigCustomData<NeonFeatureConfigCustomData> {
        override val customData: NeonFeatureConfigCustomData = NeonFeatureConfigCustomData(
            "Enables stonecutter to craft some of the woord items with fewer ingredients."
        )

        data object IsEnabled: NeonFeatureConfigProperty<Boolean>() {
            override val parentConfigKey: String = "nCutter"
            override val keyName: String = "isEnabled"
            override val description: String = "Toggle status."
            override val dataType: DataType = DataType.Boolean
            override val defaultValue: Boolean = false
            override val dataRange: ConfigDataRange<Boolean> = ConfigDataRange.DataRangeBoolean

            override fun <T: IConfigProperty> getConfigCustomData(): T? {
                return this@NCutterConfigProperty.customData as T?
            }
        }
    }

    object NSmelterConfigProperty: IConfigCustomData<NeonFeatureConfigCustomData> {
        override val customData: NeonFeatureConfigCustomData = NeonFeatureConfigCustomData(
            "Enables blast furnace to smelft some of the smeltable items that only the furnace can smelt."
        )

        data object IsEnabled: NeonFeatureConfigProperty<Boolean>() {
            override val parentConfigKey: String = "nSmelter"
            override val keyName: String = "isEnabled"
            override val description: String = "Toggle status."
            override val dataType: DataType = DataType.Boolean
            override val defaultValue: Boolean = false
            override val dataRange: ConfigDataRange<Boolean> = ConfigDataRange.DataRangeBoolean

            override fun <T: IConfigProperty> getConfigCustomData(): T? {
                return this@NSmelterConfigProperty.customData as T?
            }
        }
    }

    object NPVPConfigProperty: IConfigCustomData<NeonFeatureConfigCustomData> {
        override val customData: NeonFeatureConfigCustomData = NeonFeatureConfigCustomData(
            "Enables or disables PvP in the server."
        )

        data object IsEnabled: NeonFeatureConfigProperty<Boolean>() {
            override val parentConfigKey: String = "nPVP"
            override val keyName: String = "isEnabled"
            override val description: String = "Toggle status."
            override val dataType: DataType = DataType.Boolean
            override val defaultValue: Boolean = false
            override val dataRange: ConfigDataRange<Boolean> = ConfigDataRange.DataRangeBoolean

            override fun <T : IConfigProperty> getConfigCustomData(): T? {
                return this@NPVPConfigProperty.customData as T?
            }
        }
    }

    object NWaypointsConfigProperty: IConfigCustomData<NeonFeatureConfigCustomData> {
        override val customData: NeonFeatureConfigCustomData = NeonFeatureConfigCustomData(
            "Enables to save waypoints, remove waypoints and teleport to waypoint in the server.",
            command = "/neon waypoints"
        )

        data object IsEnabled: NeonFeatureConfigProperty<Boolean>() {
            override val parentConfigKey: String = "nWaypoints"
            override val keyName: String = "isEnabled"
            override val description: String = "Toggle status."
            override val dataType: DataType = DataType.Boolean
            override val defaultValue: Boolean = false
            override val dataRange: ConfigDataRange<Boolean> = ConfigDataRange.DataRangeBoolean

            override fun <T : IConfigProperty> getConfigCustomData(): T? {
                return this@NWaypointsConfigProperty.customData as T?
            }
        }

        data object CrossDimension: NeonFeatureConfigProperty<Boolean>() {
            override val parentConfigKey: String = "nWaypoints.options"
            override val keyName: String = "crossDimension"
            override val description: String = "Enable cross dimension teleportation."
            override val dataType: DataType = DataType.Boolean
            override val defaultValue: Boolean = false
            override val dataRange: ConfigDataRange<Boolean> = ConfigDataRange.DataRangeBoolean
        }
    }

    object NDurableConfigProperty: IConfigCustomData<NeonFeatureConfigCustomData> {
        override val customData: NeonFeatureConfigCustomData = NeonFeatureConfigCustomData(
            "Another way of handling the durability of the tools/weapons."
        )

        data object IsEnabled: NeonFeatureConfigProperty<Boolean>() {
            override val parentConfigKey: String = "nDurable"
            override val keyName: String = "isEnabled"
            override val description: String = "Toggle status."
            override val dataType: DataType = DataType.Boolean
            override val defaultValue: Boolean = false
            override val dataRange: ConfigDataRange<Boolean> = ConfigDataRange.DataRangeBoolean

            override fun <T : IConfigProperty> getConfigCustomData(): T? {
                return this@NDurableConfigProperty.customData as T?
            }
        }

        data object ShowItemDurability: NeonFeatureConfigProperty<Boolean>() {
            override val parentConfigKey: String = "nDurable.options"
            override val keyName: String = "showItemDurability"
            override val description: String = "Show/hide item durability."
            override val dataType: DataType = DataType.Boolean
            override val defaultValue: Boolean = true
            override val dataRange: ConfigDataRange<Boolean> = ConfigDataRange.DataRangeBoolean
        }
    }

    object NBundleConfigProperty: IConfigCustomData<NeonFeatureConfigCustomData> {
        override val customData: NeonFeatureConfigCustomData = NeonFeatureConfigCustomData(
            "Since the bundle is unobtainable in survival mode within Minecraft 1.17 ~ 1.20, " +
                    "it is now obtainable in survival mode."
        )

        data object IsEnabled: NeonFeatureConfigProperty<Boolean>() {
            override val parentConfigKey: String = "nBundle"
            override val keyName: String = "isEnabled"
            override val description: String = "Toggle status."
            override val dataType: DataType = DataType.Boolean
            override val defaultValue: Boolean = false
            override val dataRange: ConfigDataRange<Boolean> = ConfigDataRange.DataRangeBoolean

            override fun <T : IConfigProperty> getConfigCustomData(): T? {
                return this@NBundleConfigProperty.customData as T?
            }
        }

        data object BundleGenerateChance: NeonFeatureConfigProperty<Double>() {
            override val parentConfigKey: String = "nBundle.options"
            override val keyName: String = "bundleGenerateChance"
            override val description: String = "How frequent Bundle can generate in loot chests."
            override val dataType: DataType = DataType.Double
            override val defaultValue: Double = 0.17
            override val dataRange: ConfigDataRange<Double> = ConfigDataRange(0.001, 1.0)
        }

        data object BundleMaxBuy: NeonFeatureConfigProperty<Long>() {
            override val parentConfigKey: String = "nBundle.options"
            override val keyName: String = "bundleMaxBuy"
            override val description: String = "How many Bundle you can buy from Tannery Villager per stock."
            override val dataType: DataType = DataType.Long
            override val defaultValue: Long = 12
            override val dataRange: ConfigDataRange<Long> = ConfigDataRange(1, 64)
        }

        data object BundlePrice: NeonFeatureConfigProperty<Long>() {
            override val parentConfigKey: String = "nBundle.options"
            override val keyName: String = "bundlePrice"
            override val description: String = "How much (emeralds) per Bundle."
            override val dataType: DataType = DataType.Long
            override val defaultValue: Long = 5
            override val dataRange: ConfigDataRange<Long> = ConfigDataRange(1, 64)
        }

        data object BundlePriceMultiplier: NeonFeatureConfigProperty<Double>() {
            override val parentConfigKey: String = "nBundle.options"
            override val keyName: String = "bundlePriceMultiplier"
            override val description: String = "How much price will be multiplied when Bundle in demand."
            override val dataType: DataType = DataType.Double
            override val defaultValue: Double = 0.2
            override val dataRange: ConfigDataRange<Double> = ConfigDataRange(0.1, 1.0)
        }

        data object VilagerExperience: NeonFeatureConfigProperty<Long>() {
            override val parentConfigKey: String = "nBundle.options"
            override val keyName: String = "villagerExperience"
            override val description: String = "How much xp will villager get per trade."
            override val dataType: DataType = DataType.Long
            override val defaultValue: Long = 5
            override val dataRange: ConfigDataRange<Long> = ConfigDataRange(1, 64)
        }
    }

    object NFireworksConfigProperty: IConfigCustomData<NeonFeatureConfigCustomData> {
        override val customData: NeonFeatureConfigCustomData = NeonFeatureConfigCustomData(
            "[Experimental] Create custom firework pattern by using imported images.",
            "/neon fireworks",
            true
        )

        data object IsEnabled: NeonFeatureConfigProperty<Boolean>() {
            override val parentConfigKey: String = "nFireworks"
            override val keyName: String = "isEnabled"
            override val description: String = "Toggle status."
            override val dataType: DataType = DataType.Boolean
            override val defaultValue: Boolean = false
            override val dataRange: ConfigDataRange<Boolean> = ConfigDataRange.DataRangeBoolean

            override fun <T : IConfigProperty> getConfigCustomData(): T? {
                return this@NFireworksConfigProperty.customData as T?
            }
        }

        data object ParticleSize: NeonFeatureConfigProperty<Double>() {
            override val parentConfigKey: String = "nFireworks.options"
            override val keyName: String = "particleSize"
            override val description: String = ""
            override val dataType: DataType = DataType.Double
            override val defaultValue: Double = 1.0
            override val dataRange: ConfigDataRange<Double> = ConfigDataRange(0.1, 1.0)
        }

        data object ParticleSpeed: NeonFeatureConfigProperty<Double>() {
            override val parentConfigKey: String = "nFireworks.options"
            override val keyName: String = "particleSpeed"
            override val description: String = ""
            override val dataType: DataType = DataType.Double
            override val defaultValue: Double = 1.0
            override val dataRange: ConfigDataRange<Double> = ConfigDataRange(0.1, 1.0)
        }
    }

    object NPaintingConfigProperty: IConfigCustomData<NeonFeatureConfigCustomData> {
        override val customData: NeonFeatureConfigCustomData = NeonFeatureConfigCustomData(
            "[Experimental] (Incompatible with Minecraft 1.17.X) Create custom painting by using imported images.",
            "/neon painting",
            true
        )

        data object IsEnabled: NeonFeatureConfigProperty<Boolean>() {
            override val parentConfigKey: String = "nPainting"
            override val keyName: String = "isEnabled"
            override val description: String = "Toggle status."
            override val dataType: DataType = DataType.Boolean
            override val defaultValue: Boolean = false
            override val dataRange: ConfigDataRange<Boolean> = ConfigDataRange.DataRangeBoolean

            override fun <T : IConfigProperty> getConfigCustomData(): T? {
                return this@NPaintingConfigProperty.customData as T?
            }
        }
    }
}