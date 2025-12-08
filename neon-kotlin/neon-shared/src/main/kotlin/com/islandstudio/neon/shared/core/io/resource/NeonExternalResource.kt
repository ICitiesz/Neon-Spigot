package com.islandstudio.neon.shared.core.io.resource

import com.islandstudio.neon.shared.experimental.NeonDataFolderNew

enum class NeonExternalResource(val neonDataFolder: NeonDataFolderNew, val resourceName: String, val resourceType: ResourceType) {
    NeonDBServerConfigFile(
        NeonDataFolderNew.NeonDatabaseFolder,
        "database-config.yml",
        ResourceType.Yaml
    ),

    NeonDBServerConfigFile2(
        NeonDataFolderNew.NeonDatabaseFolder,
        "NeonDB-config.toml",
        ResourceType.Toml
    ),

    NWaypointsGlobalFile(
        NeonDataFolderNew.NWaypointsFolder,
        "nWaypoints-Global.json",
        ResourceType.Json
    ),

    NeonFeatureFile(
        NeonDataFolderNew.NeonFeatureFolder,
        "neon-feature.toml",
        ResourceType.Toml
    )
}