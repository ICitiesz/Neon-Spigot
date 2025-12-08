package com.islandstudio.neon.shared.experimental

import com.islandstudio.neon.shared.core.exception.NeonException
import com.islandstudio.neon.shared.core.initialization.IPluginContext
import com.islandstudio.neon.shared.core.io.resource.NeonInternalResource
import com.islandstudio.neon.shared.experimental.utils.coroutines.CloseableCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.io.InputStream
import java.math.BigInteger
import java.net.URL
import java.security.MessageDigest

class ResourceManagerNew(private val pluginContext: IPluginContext) {
    fun initialize() {
        CloseableCoroutineScope(Dispatchers.IO).launchJob {
            if (!pluginContext.isVersionCompatible()) {
                throw NeonException("Incompatible server version!")
            }

            // TODO: Log info

            async {
                NeonDataFolderNew.reformatVersionFolder()
            }.await()

            async {
                NeonDataFolderNew.getAllDataFolder().forEach { folder ->
                    if (!folder.exists()) folder.mkdirs()
                }
            }.await()

            // TODO: Extract Extension
        }
    }

    fun getNeonResourceAsStream(neonInternalResource: NeonInternalResource, pluginClassLoader: ClassLoader = this.javaClass.classLoader): InputStream? {
        return pluginClassLoader.getResourceAsStream(neonInternalResource.resourceURL)
    }

    fun verifyResourceChecksum(originalResourceChecksum: String, referenceResourceURL: URL): Boolean {
        val referenceResourceChecksum = getResourceChecksum(referenceResourceURL)

        return originalResourceChecksum.trim() == referenceResourceChecksum
    }

    fun getResourceChecksum(resourceURL: URL): String {
        val resourceChecksum: String

        resourceURL.openStream().use {
            val fileHashInBytes = MessageDigest.getInstance("MD5").digest(it.readAllBytes())

            resourceChecksum = BigInteger(1, fileHashInBytes).toString(16)
        }

        return resourceChecksum
    }
}