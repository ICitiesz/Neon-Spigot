package com.islandstudio.neon.shared.core.resource

import com.islandstudio.neon.shared.core.resource.folder.NeonDataFolder

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