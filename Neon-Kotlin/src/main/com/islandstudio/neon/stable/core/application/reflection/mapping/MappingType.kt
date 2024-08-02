package com.islandstudio.neon.stable.core.application.reflection.mapping

enum class MappingType {
    FIELDS,
    METHODS,
    CONSTRUCTORS,
    CLASSES,
    UNKOWNN;

    companion object {
        fun valueOfMappingType(value: String): MappingType? {
            return MappingType.entries.find { it.name.equals(value, true) }
        }
    }
}