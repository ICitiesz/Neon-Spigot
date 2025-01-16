package com.islandstudio.neon.stable.features.nServerFeatures.properties

import com.islandstudio.neon.stable.core.io.ConfigurationProperty

data class ServerFeatureOption(private val serverFeatureOptionPropertyData: Map.Entry<String, Any>) {
    val featureName: String = serverFeatureOptionPropertyData.key

    val featureOptions: ArrayList<OptionDetail> = with(serverFeatureOptionPropertyData.value as ArrayList<*>) {
        @Suppress("UNCHECKED_CAST")
        this.map {
            val optionPropertyDetailData = it as LinkedHashMap<String, Any>

            OptionDetail(optionPropertyDetailData.entries.first().toPair())
        }.toCollection(ArrayList())
    }

    @Suppress("UNCHECKED_CAST")
    data class OptionDetail(private val optionPropertyDetailData: Pair<String, Any>) {
        private val configSection = optionPropertyDetailData.second as LinkedHashMap<String, Any>
        private val optionDataRange: LinkedHashMap<String, Any> = configSection[ConfigurationProperty.DATA_RANGE] as LinkedHashMap<String, Any>

        val optionName: String = optionPropertyDetailData.first
        val optionDescription: String = configSection[ConfigurationProperty.DESCRIPTION] as String
        val optionDataType: String = configSection[ConfigurationProperty.DATA_TYPE] as String
        val optionDefaultValue: Any = configSection[ConfigurationProperty.DEFAULT_VALUE] as Any
        val optionMinValue: Any? = if (optionDataRange.isNotEmpty()) optionDataRange["minValue"] else null
        val optionMaxValue: Any? = if (optionDataRange.isNotEmpty()) optionDataRange["maxValue"] else null
    }
}