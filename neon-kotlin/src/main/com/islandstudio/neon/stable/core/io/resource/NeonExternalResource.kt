package com.islandstudio.neon.stable.core.io.resource

import com.islandstudio.neon.stable.core.io.nFile.NeonDataFolder

enum class NeonExternalResource(val neonDataFolder: NeonDataFolder, val resourceName: String, val resourceType: ResourceType) {
    NeonDBServerConfigFile(
        NeonDataFolder.NeonDatabaseFolder,
        "database-config.yml",
        ResourceType.Yaml
    ),

    NeonDBServerConfigFile2(
        NeonDataFolder.NeonDatabaseFolder,
        "NeonDB-config.toml",
        ResourceType.Toml
    )
}