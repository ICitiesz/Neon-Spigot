package com.islandstudio.neon.stable.core.application

import com.islandstudio.neon.stable.core.io.nFile.NeonDataFolder
import com.islandstudio.neon.stable.core.io.resource.NeonResources
import java.io.File

sealed class NeonExtensions(extension: File): File(extension.toURI()) {
    data class NeonDatabaseExtension(
        val extension: File = File(
            NeonDataFolder.ExtensionFolder(),
            NeonResources.NEON_DATABASE_EXTENSION.getResourceName()
        )
    ): NeonExtensions(extension)
}