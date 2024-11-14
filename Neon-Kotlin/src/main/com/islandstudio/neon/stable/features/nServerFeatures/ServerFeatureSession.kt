package com.islandstudio.neon.stable.features.nServerFeatures

import com.islandstudio.neon.stable.core.io.DataSourceType
import com.islandstudio.neon.stable.features.nServerFeatures.properties.ServerFeature
import com.islandstudio.neon.stable.features.nServerFeatures.properties.ServerFeatureDetail
import com.islandstudio.neon.stable.features.nServerFeatures.properties.ServerFeatureOption

class ServerFeatureSession{
    private val sourceServerFeature: ArrayList<ServerFeature> by lazy { ArrayList() }
    private val activeServerFeature: ArrayList<ServerFeature> by lazy { ArrayList() }
    private val sourceServerFeatureOption: ArrayList<ServerFeatureOption> by lazy { ArrayList() }
    private val serverFeatureDetail: ArrayList<ServerFeatureDetail> by lazy { ArrayList() }

    fun initServerFeature(serverFeature: ArrayList<ServerFeature>, dataSourceType: DataSourceType) {
        when(dataSourceType) {
            DataSourceType.INTERNAL_SOURCE -> {
                if (sourceServerFeature.isNotEmpty()) {
                    sourceServerFeature.clear()
                }

                sourceServerFeature.addAll(serverFeature)
            }

            DataSourceType.EXTERNAL_SOURCE -> {
                if (activeServerFeature.isNotEmpty()) {
                    activeServerFeature.clear()
                }

                activeServerFeature.addAll(serverFeature)
            }
        }
    }

    fun initServerFeatureDetail(serverFeatureDetails: ArrayList<ServerFeatureDetail>) {
        this.serverFeatureDetail.addAll(serverFeatureDetails)
    }

    fun initServerFeatureOption(serverFeatureOption: ArrayList<ServerFeatureOption>) {
        sourceServerFeatureOption.clear()
        sourceServerFeatureOption.addAll(serverFeatureOption)
    }

    fun setServerFeatureToggle(featureName: String, newToggleValue: Boolean): Boolean {
        activeServerFeature.find { it.featureName == featureName }?.let {
            it.isEnabled = newToggleValue
            return true
        }

        return false
    }

    fun updateServerFeatures(newServerFeature: ArrayList<ServerFeature>) {
        activeServerFeature.clear()
        activeServerFeature.addAll(newServerFeature)
    }

    fun setServerFeatureOptionValue(featureName: String, optionName: String, newOptionValue: Any): Boolean {
        activeServerFeature.find { it.featureName == featureName }?.let {
            it.options.find { option -> option.optionName == optionName }?.let { option ->
                option.optionValue = newOptionValue
                return true
            }

            return false
        }

        return false
    }

    fun getServerFeatureList(dataSourceType: DataSourceType): ArrayList<ServerFeature> {
        return when(dataSourceType) {
            DataSourceType.INTERNAL_SOURCE -> {
                sourceServerFeature
            }

            DataSourceType.EXTERNAL_SOURCE -> {
                activeServerFeature
            }
        }
    }

    fun getServerFeatureDescription(featureName: String, includeTag: Boolean = true): String? {
        val serverFeatureDetail = getServerFeatureDetail(featureName) ?: return null
        val serverFeatureDescription = serverFeatureDetail.featureDescription

        if (includeTag) return serverFeatureDescription

        if (!serverFeatureDescription.startsWith("[Experimental]", true)) return serverFeatureDescription

        return serverFeatureDescription.substringAfter("[Experimental]").trimStart()
    }

    fun getServerFeatureCommand(featureName: String, includeTag: Boolean= true): String? {
        val serverFeatureDetail = getServerFeatureDetail(featureName) ?: return null
        val serverFeatureCommand = serverFeatureDetail.featureCommand ?: return null

        if (includeTag) return serverFeatureCommand

        if (!serverFeatureCommand.startsWith("Command:", true)) return serverFeatureCommand

        return serverFeatureCommand.substringAfter("Command:").trimStart()
    }

    fun getActiveServerFeatureToggle(serverFeatureName: String): Boolean? {
        val activeServerFeature = getActiveServerFeature(serverFeatureName) ?: return null

        return activeServerFeature.isEnabled
    }

    fun getServerFeature(serverFeatureName: String): ServerFeature? {
        return sourceServerFeature.find { it.featureName == serverFeatureName }
    }

    fun getActiveServerFeatureOptionValue(serverFeatureName: String, optionName: String): Any? {
        val activeServerFeature = getActiveServerFeature(serverFeatureName) ?: return null

        return activeServerFeature.options.find { it.optionName == optionName }?.optionValue
    }

    fun isExperimental(featureName: String): Boolean {
        getServerFeatureDescription(featureName)?.let {
            return it.startsWith("[Experimental]", true)
        }

        return false
    }

    fun getOptionDescription(serverFeatureName: String, optionName: String): String? {
        return getServerFeatureOption(serverFeatureName, optionName)?.optionDescription
    }

    fun getOptionDataType(serverFeatureName: String, optionName: String): String? {
        return getServerFeatureOption(serverFeatureName, optionName)?.optionDataType
    }

    fun getOptionDefaultValue(serverFeatureName: String, optionName: String): Any? {
        return getServerFeatureOption(serverFeatureName, optionName)?.optionDefaultValue
    }

    fun getOptionMinValue(serverFeatureName: String, optionName: String): Any? {
        return getServerFeatureOption(serverFeatureName, optionName)?.optionMinValue
    }

    fun getOptionMaxValue(serverFeatureName: String, optionName: String): Any? {
        return getServerFeatureOption(serverFeatureName, optionName)?.optionMaxValue
    }

    fun getAllServerFeatureOption(): ArrayList<ServerFeatureOption> {
        return sourceServerFeatureOption
    }

    fun getServerFeatureOptionList(serverFeatureName: String): ArrayList<ServerFeatureOption.OptionDetail> {
        return sourceServerFeatureOption.find { it.featureName == serverFeatureName }?.featureOptions ?: ArrayList()
    }

    fun getServerFeatureOption(serverFeatureName: String, optionName: String): ServerFeatureOption.OptionDetail? {
        val optionParent = sourceServerFeatureOption
            .find { it.featureName == serverFeatureName } ?: return null

        return optionParent.featureOptions.find { it.optionName == optionName }
    }

    private fun getActiveServerFeature(featureName: String): ServerFeature? {
        return activeServerFeature.find { it.featureName == featureName}
    }

    private fun getServerFeatureDetail(featureName: String): ServerFeatureDetail? {
        return serverFeatureDetail.find { it.featureName == featureName }
    }
}