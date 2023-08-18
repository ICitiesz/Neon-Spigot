package com.islandstudio.neon.stable.primary.nServerFeatures

import org.simpleyaml.configuration.ConfigurationSection
import org.simpleyaml.configuration.file.YamlFile
import java.io.Serializable

class ServerFeature {
    enum class ServerFeatureProperty(val property: String) {
        IS_EXPERIMENTAL("isExperimental"),
        DESCRIPTION("description"),
        IS_ENABLED("isEnabled"),
        OPTIONS("options"),
        COMMAND("command");
    }

    enum class FeatureNames(val featureName: String) {
        N_CUTTER("nCutter"),
        N_WAYPOINTS("nWaypoints"),
        N_HARVEST("nHarvest"),
        N_SMELTER("nSmelter"),
        N_PVP("nPVP"),

        /* Experimental */
        N_DURABLE("nDurable"),
        N_BUNDLE("nBundle")
    }

    data class SerializableFeature(@Transient private val serverFeatureData: Map.Entry<String, Any>): Serializable {
        @Transient
        private val serverFeatureDetails = serverFeatureData.value as ConfigurationSection
        private val featureName: String = serverFeatureData.key
        var isExperimental: Boolean? = serverFeatureDetails.getBoolean(ServerFeatureProperty.IS_EXPERIMENTAL.property)
        var description: String? = serverFeatureDetails.getString(ServerFeatureProperty.DESCRIPTION.property)
        var isEnabled: Boolean? = serverFeatureDetails.getBoolean(ServerFeatureProperty.IS_ENABLED.property)
        var command: String? = serverFeatureDetails.getString(ServerFeatureProperty.COMMAND.property)
        var options: HashMap<String, SerializableOption>? = HashMap()

        init {
            serverFeatureDetails.get(ServerFeatureProperty.OPTIONS.property).let {
                if (it !is ConfigurationSection) {
                    options = null
                    return@let
                }

                it.getValues(false).forEach { optionData ->
                    options!![optionData.key] = SerializableOption(featureName, optionData)
                }
            }
        }

        data class SerializableOption(val featureName: String, @Transient private val optionData: Map.Entry<String, Any>): Serializable {
            @Transient
            private val optionProperties: YamlFile = NServerFeatures.Handler.getOptionProperties()

            val optionName: String = optionData.key
            var optionValue: Any = optionData.value

            var optionDataType: String = ""
            var optionDescription: String = ""
            var optionDataRange: Array<String> = arrayOf("", "")

            var hasDataRange: Boolean = false

            init {
                if (optionProperties.getKeys(false).contains(featureName)) {
                    optionDataType = (optionProperties.getString("${featureName}.${optionName}.dataType") ?: "")
                    optionDescription = (optionProperties.getString("${featureName}.${optionName}.description") ?: "")

                    if (optionProperties["${featureName}.${optionName}.dataRange"] != null) {
                        hasDataRange = true
                        optionDataRange[0] =  optionProperties["${featureName}.${optionName}.dataRange.minValue"].toString()
                        optionDataRange[1] = optionProperties["${featureName}.${optionName}.dataRange.maxValue"].toString()
                    }

                }
            }
        }
    }

    data class Feature(private val serverFeatures: Any) {
        private lateinit var serverFeatureDetails: ConfigurationSection

        val name: String?
        val description: String?
        val isEnabled: Boolean?
        val command: String?
        val option: ConfigurationSection?

        init {
            if (serverFeatures is Map.Entry<*, *>) serverFeatureDetails = serverFeatures.value as ConfigurationSection

            if (serverFeatures is ConfigurationSection) serverFeatureDetails = serverFeatures

            name = serverFeatureDetails.name
            description = serverFeatureDetails.getString(ServerFeatureProperty.DESCRIPTION.property)
            isEnabled = serverFeatureDetails.getBoolean(ServerFeatureProperty.IS_ENABLED.property)
            command = serverFeatureDetails.getString(ServerFeatureProperty.COMMAND.property)
            option = serverFeatureDetails.get(ServerFeatureProperty.OPTIONS.property).let { if (it is ConfigurationSection) it else null }
        }
    }
}

