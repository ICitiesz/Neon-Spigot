package com.islandstudio.neon.stable.core.application.reflection.mapping

enum class MappingType {
    FIELD,
    METHOD,
    CONSTRUCTOR,
    CLASS,
    UNKOWNN;

    companion object {
        fun valueOfMappingType(value: String): MappingType {
            return MappingType.entries.find { it.name.equals(value, true) } ?: UNKOWNN
        }
    }
}