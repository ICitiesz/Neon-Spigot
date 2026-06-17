package com.islandstudio.neon.rework.core.nms

import com.islandstudio.neon.core.nmsmapping.type.NmsClass
import com.islandstudio.neon.core.nmsmapping.type.NmsConstructor
import com.islandstudio.neon.core.nmsmapping.type.NmsField
import com.islandstudio.neon.core.nmsmapping.type.NmsMethod

interface INmsMapper {
    companion object {
        private val mappingRegistry = NmsManagerRework.getMappingRegistry()
    }

    fun mapField(nmsField: NmsField): String = mappingRegistry.getNmsField(nmsField)

    fun mapMethod(nmsMethod: NmsMethod): String = mappingRegistry.getNmsMethod(nmsMethod)

    fun mapConstructor(nmsConstructor: NmsConstructor): String = mappingRegistry.getNmsConstructor(nmsConstructor)

    fun mapClass(nmsClass: NmsClass): String = mappingRegistry.getNmsClass(nmsClass)
}