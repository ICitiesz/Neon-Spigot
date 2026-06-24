package com.islandstudio.neon.shared.rework.core.io.resource

import com.islandstudio.neon.shared.rework.core.io.DataDirectory
import java.io.File

enum class ExternalResource(val resourceFolder: File, val resourceName: String) {
    DatabaseConfig(DataDirectory.NeonDatabaseFolderNew, "database-config.toml")
}