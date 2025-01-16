package com.islandstudio.neon.stable.features.nServerFeatures.properties

import com.islandstudio.neon.stable.core.io.ConfigurationProperty
import org.simpleyaml.configuration.MemorySection

data class ServerFeature(private val serverFeatureData: Map.Entry<String, Any>) {
    private val configSection = serverFeatureData.value as MemorySection
    val featureName: String = serverFeatureData.key
    var isEnabled: Boolean? = configSection.getBoolean(ConfigurationProperty.IS_ENABLED)
    val options: ArrayList<ServerOption> = with(configSection.getList(ConfigurationProperty.OPTIONS)) {
        if (this.isNullOrEmpty()) return@with arrayListOf()

        this.filterIsInstance<LinkedHashMap<*, *>>().map {
            @Suppress("UNCHECKED_CAST")
            val serverOptionData = it as LinkedHashMap<String, Any>

            ServerOption(serverOptionData.entries.map { entry -> entry.toPair() }.first())
        }.toCollection(ArrayList())
    }

    data class ServerOption(private val serverOptionData: Pair<String, Any>) {
        val optionName: String = serverOptionData.first
        var optionValue: Any? = serverOptionData.second
    }

    fun setServerOptionValue(optionName: String, optionValue: Any): Boolean {
        options.find { it.optionName == optionName }?.let {
            it.optionValue = optionValue
            return true
        } ?: return false
    }
}
