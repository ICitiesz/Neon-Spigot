package com.islandstudio.neon.core.nmsmapping

data class NmsObject(
    val mappingType: MappingType,
    val objectName: String,
    val remappedName: String,
    val version: String
)
