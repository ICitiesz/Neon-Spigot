package com.islandstudio.neon.features.neonfeature.gui

import com.islandstudio.neon.experimental.gui.state.GuiStateData
import com.islandstudio.neon.features.neonfeature.NeonFeatureManager
import com.islandstudio.neon.shared.core.config.component.ConfigDataRange
import com.islandstudio.neon.shared.core.config.component.ConfigNodeProperty
import com.islandstudio.neon.shared.core.config.property.NeonFeatureConfigProperty
import com.islandstudio.neon.shared.core.config.property.custom.NeonFeatureConfigCustomData

data class NeonFeatureGuiStateData(
    private val neonFeatureManager: NeonFeatureManager
): GuiStateData {
    val featureConfigReference: ArrayList<NeonFeatureConfigProperty<*>> = neonFeatureManager.getFeatureConfigProperties() // NOTE: This should be at first or else everything broken
    val featureConfig: ArrayList<ConfigNodeProperty> = neonFeatureManager.getFeatureConfigNodeProperties()
    val features = featureConfig
        .filter { x -> !x.parentConfigNode().toString().contains(".options") }
        .toCollection(ArrayList())

    private var featureSortType: NeonFeatureSortType = NeonFeatureSortType.Default
    private var selectedFeature: String? = null

    fun selectFeature(featureName: String?): Boolean {
        if (featureName == null) {
            selectedFeature = null
            return false
        }

        getFeatureOptions(featureName).ifEmpty { return false }

        selectedFeature = featureName
        return true
    }

    fun getSelectedFeature(): String? {
        return selectedFeature
    }

    fun getCurrentSortType(): NeonFeatureSortType = featureSortType

    fun setSortType(sortType: NeonFeatureSortType) {
        featureSortType = sortType
    }

    fun getFeatureName(feature: ConfigNodeProperty): String {
        return feature.parentConfigNode().toString().removeSurrounding("[", "]")
    }

    fun getFeatureDescription(featureName: String): String? {
        return featureConfigReference
            .filter { !it.parentConfigKey.endsWith(".options") }
            .find { it.parentConfigKey == featureName }
            ?.getConfigCustomData<NeonFeatureConfigCustomData>()
            ?.featureDescription
    }

    fun getFeatureExperimentalStatus(featureName: String): Boolean {
        return featureConfigReference
            .filter { !it.parentConfigKey.endsWith(".options") }
            .find { it.parentConfigKey == featureName }
            ?.getConfigCustomData<NeonFeatureConfigCustomData>()
            ?.isExperimental ?: false
    }

    fun getFeatureCommand(featureName: String): String? {
        return featureConfigReference
            .filter { !it.parentConfigKey.endsWith(".options") }
            .find { it.parentConfigKey == featureName }
            ?.getConfigCustomData<NeonFeatureConfigCustomData>()
            ?.command
    }

    fun getFeatureOptions(featureName: String?): ArrayList<ConfigNodeProperty> {
        return featureName?.let { x ->
            featureConfig
                .filter { it.parentConfigNode().toString().contains(".options")
                        && it.parentConfigNode().toString().removeSurrounding("[", "]") == "${x}.options"
                }.toCollection(ArrayList())
        } ?: arrayListOf()
    }

    fun getFeatureOptionDescription(featureOptionName: String): String? {
        return featureConfigReference
            .filter { it.parentConfigKey.endsWith(".options") }
            .find { it.parentConfigKey == "${selectedFeature}.options"
                    && it.keyName == featureOptionName
            }
            ?.description
    }

    fun getFeatureOptionDefaultValue(featureOption: ConfigNodeProperty): Any {
        return getFeatureOptionConfigReference(featureOption)?.defaultValue ?: "Unknown"
    }

    fun getFeatureOptionDataRange(feautureOption: ConfigNodeProperty): ConfigDataRange<*>? {
        return getFeatureOptionConfigReference(feautureOption)?.dataRange
    }

    private fun getFeatureOptionConfigReference(featureOption: ConfigNodeProperty): NeonFeatureConfigProperty<*>? {
        return featureConfigReference
            .find {
                it.parentConfigKey == featureOption.parentConfigNode().toString().removeSurrounding("[", "]")
                        && it.keyName == featureOption.key()
            }
    }
}