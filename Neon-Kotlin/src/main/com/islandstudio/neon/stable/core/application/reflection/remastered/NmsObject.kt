package com.islandstudio.neon.stable.core.application.reflection.remastered

data class NmsObject(
    val mappingType: MappingType,
    val objectName: String,
    val remappedName: String,
    val version: String
)
