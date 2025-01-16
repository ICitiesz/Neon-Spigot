package com.islandstudio.neon.shared.core.io.resource

import com.islandstudio.neon.shared.core.io.folder.NeonDataFolder
import java.io.File

sealed class NeonExtensions(extension: File): File(extension.toURI()) {
    data object NeonDatabaseExtension: NeonExtensions(File(
        NeonDataFolder.ExtensionFolder,
        NeonInternalResource.NeonDatabaseServer.getResourceName()
    )) {
        private fun readResolve(): Any = NeonDatabaseExtension
    }
}