package com.islandstudio.neon.experimental.config

import com.akuleshov7.ktoml.Toml
import com.akuleshov7.ktoml.TomlIndentation
import com.akuleshov7.ktoml.TomlInputConfig
import com.akuleshov7.ktoml.TomlOutputConfig
import com.akuleshov7.ktoml.exceptions.TomlDecodingException
import com.akuleshov7.ktoml.tree.nodes.TomlFile
import com.akuleshov7.ktoml.tree.nodes.TomlKeyValuePrimitive
import com.akuleshov7.ktoml.tree.nodes.TomlNode
import com.akuleshov7.ktoml.tree.nodes.TomlTable
import com.islandstudio.neon.experimental.config.component.ConfigNodeProperty
import com.islandstudio.neon.experimental.config.component.ConfigProperty
import com.islandstudio.neon.experimental.config.component.type.AbstractConfigWrapper
import com.islandstudio.neon.experimental.config.component.type.IConfigObject
import com.islandstudio.neon.experimental.config.component.type.IConfigProperty
import com.islandstudio.neon.experimental.utils.DataUtil
import com.islandstudio.neon.stable.core.application.exceptions.ExceptionSearchMessages
import com.islandstudio.neon.stable.core.application.exceptions.NeonConfigException
import com.islandstudio.neon.stable.core.io.nFile.NeonDataFolder
import com.islandstudio.neon.stable.core.io.resource.NeonExternalResource
import com.islandstudio.neon.stable.utils.processing.GeneralInputProcessor
import com.islandstudio.neon.stable.utils.processing.TextProcessor
import com.islandstudio.neon.stable.utils.processing.properties.DataTypes
import kotlinx.serialization.encodeToString
import kotlinx.serialization.serializer
import java.io.File
import kotlin.reflect.full.createType

class AppConfig<T: AbstractConfigWrapper<U, V>, U: IConfigObject, V: IConfigProperty>(
    neonExternalResource: NeonExternalResource,
    val configWrapper: T,
    inputOption: TomlInputConfig = TomlInputConfig(true, allowEscapedQuotesInLiteralStrings = true),
    outputOption : TomlOutputConfig = TomlOutputConfig(TomlIndentation.TWO_SPACES)
) {
    private val tomlInstance = Toml(inputConfig = inputOption, outputConfig = outputOption)
    private val configFile = NeonDataFolder.createNewFile(neonExternalResource)

    init {
        initialize().apply {
            @Suppress("UNCHECKED_CAST")
            configWrapper.initConfigObject(this as U)
        }
    }

    private fun initialize(): IConfigObject {
        if (configFile.length() == 0L) {
            val defaultConfigObject = configWrapper.getDefaultConfigObject()

            saveToFile(defaultConfigObject)
            return defaultConfigObject
        }

        updateConfig(configFile, configWrapper.getAllConfigProperty()).apply {
            return decodeFromString(encodeToString(this))
        }
    }

    /**
     * Get config node properties
     *
     * @param configNode
     * @return
     */
    private fun getConfigNodeProperties(configNode: TomlFile): MutableList<ConfigNodeProperty> {
        return extractConfigNode(configNode).map {
            ConfigNodeProperty(it as TomlKeyValuePrimitive)
        }.toMutableList()
    }

    /**
     * Extract config node from the parsed config which also root node
     *
     * @param rootConfigNode
     * @return
     */
    private fun extractConfigNode(rootConfigNode: TomlFile): List<TomlNode> {
        val configNodes: ArrayList<TomlNode> = arrayListOf(rootConfigNode)

        while (configNodes.any { x -> x is TomlTable || x is TomlFile }) {
            configNodes.toCollection(ArrayList()).forEach {
                if (it is TomlTable || it is TomlFile) {
                    configNodes.remove(it)
                    configNodes.addAll(it.children)
                    return@forEach
                }

                if (configNodes.contains(it)) return@forEach

                configNodes.add(it)
            }
        }

        return configNodes
    }

    fun saveToFile(configObject: IConfigObject) {
        configFile.writeText(encodeToString(configObject))
    }

    /**
     * Update config
     *
     * @param writableConfigFile
     * @param configProperties
     * @return
     */
    private fun updateConfig(writableConfigFile: File, configProperties: ArrayList<ConfigProperty>): TomlFile {
        val parsedConfig = parseToTomlFile(writableConfigFile)

        return rebuildConfig(parsedConfig, configProperties)
    }

    /**
     * Rebuild config to Toml File with resolved config value.
     *
     * @param parsedConfig
     * @param configProperties
     * @return
     */
    private fun rebuildConfig(parsedConfig: TomlFile, configProperties: ArrayList<ConfigProperty>): TomlFile {
        val newConfig = TomlFile()

        getConfigNodeProperties(parsedConfig).forEach { configNodeProperty ->
            val configKey = configNodeProperty.key()
            val parentConfigKey = configNodeProperty.parentConfigNode()?.let {
                if (it is TomlTable) return@let it.fullTableKey.toString()

                it.name
            } ?: return@forEach

            val configProperty = configProperties.find {
                parentConfigKey == it.parentConfigKey && configKey == it.keyName
            } ?: return@forEach

            /* Config value validation */
            validateConfigValue(configNodeProperty, configProperty)

            val configNode = configNodeProperty.buildConfigNode()

            /* Get the parent node and append all the config node to that parent */
            configNodeProperty.parentConfigNode()?.let {
                if (newConfig.children.contains(it)) {
                    it.appendChild(configNode)
                    return@let
                }

                it.children.clear()
                it.appendChild(configNode)

                newConfig.appendChild(it)
            }
        }

        return newConfig
    }

    /**
     * Perform validation to the config value and try to resolve it with default value
     * if any of the values are mismatch.
     *
     * @param configNodeProperty
     * @param configProperty
     */
    private fun validateConfigValue(configNodeProperty: ConfigNodeProperty, configProperty: ConfigProperty) {
        val configDataType = configNodeProperty.dataType()
        val configValue = configNodeProperty.value()

        val refConfigDataType = configProperty.dataType
        val defaultConfigValue = configProperty.defaultValue
        val configDataRange = configProperty.dataRange

        /* Validate and resolve data type if mismatch data type */
        if (configDataType != refConfigDataType) {
            val newConfigValue = DataUtil.convertDataType(configValue, refConfigDataType)
                ?: defaultConfigValue

            configNodeProperty.updateConfigNodeValue(newConfigValue)
        }

        /* Validate and resolve data range if mismatch data range */
        if (!DataUtil.validateDataRange(configValue, configProperty.dataType, configDataRange.minValue, configDataRange.maxValue)) {
            configNodeProperty.updateConfigNodeValue(configProperty.defaultValue)
        }
    }

    /**
     * Try to resolve any parse error.
     *
     * @param configContent
     * @param parseResult
     * @return
     */
    private fun tryResolveParseError(configContent: MutableList<String>, parseResult: Result<*>): MutableList<String>{
        val parseException = parseResult.exceptionOrNull() ?: throw NeonConfigException()

        if (parseException !is TomlDecodingException) {
            parseException.message?.let {
                throw NeonConfigException(it, it.javaClass)
            } ?: throw NeonConfigException(externalCauseBy = parseException.javaClass)
        }

        /* Try to get the string line number that cause parse error */
        val errorLineNo = parseException.message?.let { stringChar ->
            Regex("""[Ll]ine:? <?(\d+)>?""").find(stringChar)?.groupValues?.get(1)?.toInt()
        } ?: throw NeonConfigException("Error while trying to get error line number from config file!")

        /* Handle those error string by category */
        when {
            // Error Case 1: Incorrect format key-value pair (missing equals sign) | E.g: keyName
            TextProcessor.validateExceptionMessage(parseException.message!!, ExceptionSearchMessages.TomlParseExceptionIncorrectFormat) -> {
                configContent[errorLineNo - 1] = "${configContent[errorLineNo - 1].trimEnd()} = \"\""
            }

            // Error Case 2: Invalid key spaces | E.g: keyName keyValue
            TextProcessor.validateExceptionMessage(parseException.message!!, ExceptionSearchMessages.TomlParseExceptionInvalidSpaces) -> {
                configContent[errorLineNo - 1] = with (configContent[errorLineNo - 1].split(" ").filter { it.isNotEmpty() }) {
                    "${this.first()} = \"${this[1]}\""
                }
            }

            // Error Case 3: String value not wrapped or quoted | E.g: keyName = stringValue
            TextProcessor.validateExceptionMessage(parseException.message!!, ExceptionSearchMessages.TomlParseExceptionStringValueNotWrapped) -> {
                val searchDelimiter = "="
                val replacedValue = configContent[errorLineNo - 1].substringAfter(searchDelimiter).trimIndent().run {
                    return@run GeneralInputProcessor.convertDataType(this, DataTypes.BOOLEAN)?.let {
                        it as Boolean
                    } ?: "\"${this}\""
                }

                configContent[errorLineNo - 1] = configContent[errorLineNo - 1].replaceAfter(searchDelimiter, " $replacedValue")
            }

            else -> {
                throw NeonConfigException(parseException.message!!, externalCauseBy = parseException.javaClass)
            }
        }

        return configContent
    }

    /**
     * Parse config file content into toml file
     *
     * @param configFile
     * @return TomlFile
     */
    private fun parseToTomlFile(configFile: File): TomlFile {
        var configContent = configFile
            .reader()
            .use {
                it.readLines().toMutableList()
            }

        /* Try to parse and resolve any parse error if possible */
        while (true) {
            val parseResult = runCatching {
                tomlInstance.tomlParser.parseLines(configContent.asSequence())
            }.onSuccess {
                return it
            }

            configContent = tryResolveParseError(configContent, parseResult)
        }
    }

    private fun encodeToString(tomlFile: TomlFile): String {
        return tomlInstance.tomlWriter.writeToString(tomlFile)
    }

    private fun encodeToString(configObject: IConfigObject): String {
        return tomlInstance.encodeToString(configObject)
    }

    private fun decodeFromString(configContent: String): IConfigObject {
        return tomlInstance
            .decodeFromString(
                serializer(configWrapper.getDefaultConfigObject()::class.createType()),
                configContent
            ) as IConfigObject
    }
}