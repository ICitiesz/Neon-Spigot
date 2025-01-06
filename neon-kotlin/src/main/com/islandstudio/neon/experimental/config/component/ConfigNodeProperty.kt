package com.islandstudio.neon.experimental.config.component

import com.akuleshov7.ktoml.tree.nodes.TomlKeyValuePrimitive
import com.akuleshov7.ktoml.tree.nodes.TomlNode
import com.akuleshov7.ktoml.tree.nodes.pairs.values.*
import com.islandstudio.neon.stable.utils.processing.properties.DataTypes

data class ConfigNodeProperty(private var configNode: TomlKeyValuePrimitive) {
    private var parentConfigNode = configNode.parent
    private var key = configNode.name
    private var value: Any = configNode.value.content
    private var dataType: DataTypes = toDataType(configNode.value)
    private var comments: List<String> = configNode.comments
    private var inlineComment = configNode.inlineComment

    fun parentConfigNode(): TomlNode? = parentConfigNode

    fun key(): String = key

    fun value(): Any = value

    fun dataType(): DataTypes = dataType

    fun comments(): List<String> = comments

    fun inlineComment(): String? = inlineComment

    fun updateConfigNodeValue(value: Any) {
        this.value = value
    }

    fun buildConfigNode(): TomlKeyValuePrimitive {
        configNode = TomlKeyValuePrimitive(
            key to value.toString(),
            configNode.lineNo,
            comments,
            inlineComment
        )

        key = configNode.name
        value = configNode.value.content
        dataType = toDataType(configNode.value)
        comments = configNode.comments
        inlineComment = configNode.inlineComment

        return configNode
    }

    private fun toDataType(tomlValue: TomlValue): DataTypes {
        return when(tomlValue) {
            is TomlBasicString, is TomlLiteralString -> DataTypes.STRING

            is TomlBoolean -> DataTypes.BOOLEAN

            is TomlLong -> DataTypes.LONG

            is TomlDouble -> DataTypes.DOUBLE

            else -> DataTypes.UNSUPPORTED_DATA_TYPE
        }
    }
}