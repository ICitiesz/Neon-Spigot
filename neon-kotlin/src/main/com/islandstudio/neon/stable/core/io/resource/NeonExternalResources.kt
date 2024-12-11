package com.islandstudio.neon.stable.core.io.resource

import com.islandstudio.neon.stable.core.io.nFile.NeonDataFolder

enum class NeonExternalResources(val neonDataFolder: NeonDataFolder, val resourceName: String, val resourceType: ResourceType) {
    NeonDatabaseServerConfigFile(
        NeonDataFolder.NeonDatabaseFolder,
        "database-config.yml",
        ResourceType.YAML
    ),


}