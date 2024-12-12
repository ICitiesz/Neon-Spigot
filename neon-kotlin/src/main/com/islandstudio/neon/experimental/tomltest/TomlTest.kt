package com.islandstudio.neon.experimental.tomltest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.toml.TomlFactory
import com.islandstudio.neon.stable.core.io.nFile.NeonDataFolder
import com.islandstudio.neon.stable.core.io.resource.NeonExternalResource
import com.islandstudio.neon.stable.core.io.resource.NeonInternalResource
import com.islandstudio.neon.stable.core.io.resource.ResourceManager

object TomlTest {
    fun runTomlTest() {
        val tomlFile = run {
            val externalTomlFile = NeonDataFolder.createNewFile(NeonExternalResource.NeonDBServerConfigFile2)

            if (externalTomlFile.length() != 0L) {
                return@run externalTomlFile
            }

            val resourceManager = ResourceManager()
            val tomlFileResource = resourceManager.getNeonResourceAsUrl(NeonInternalResource.NeonDBServerConfig)

            resourceManager.copyResource(tomlFileResource!!, externalTomlFile)

            return@run externalTomlFile
        }

        val tomlMapper = ObjectMapper(TomlFactory())
        val rootNode = tomlMapper.readTree(tomlFile)

        println("TomlRoot: $rootNode")
    }
}