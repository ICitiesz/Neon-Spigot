package com.islandstudio.neon.stable.core.application.extension

import com.islandstudio.neon.stable.core.io.nFile.NeonDataFolder
import com.islandstudio.neon.stable.core.io.resource.NeonInternalResource
import java.io.File

sealed class NeonExtensions(extension: File): File(extension.toURI()) {
    data class NeonDatabaseExtension(
        val extension: File = File(
            NeonDataFolder.ExtensionFolder,
            NeonInternalResource.NeonDatabaseServer.getResourceName()
        )
    ): NeonExtensions(extension)
}