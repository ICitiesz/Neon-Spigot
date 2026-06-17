package com.islandstudio.neon.shared.rework.core.io

import com.islandstudio.neon.shared.core.exception.NeonException
import com.islandstudio.neon.shared.core.initialization.IPluginContext
import com.islandstudio.neon.shared.core.io.resource.NeonInternalResource
import java.io.InputStream
import java.math.BigInteger
import java.net.URL
import java.security.MessageDigest

class ResourceManagerRework(private val pluginContext: IPluginContext) {
    // May use suspend fun in the future as the extract extension may take long operation time
    suspend fun initialize() {
        if (!pluginContext.isVersionCompatible()) {
            throw NeonException("Incompatible server version!")
        }

        pluginContext.getPluginLogger().info("Initializing required folders...")

        NeonDataFolderRework.reformatVersionFolder()
        NeonDataFolderRework.getAllDataFolder().forEach { folder ->
            if (!folder.exists()) folder.mkdirs()
        }

        // TODO: Extract Extension
    }

    fun getNeonResourceAsStream(neonInternalResource: NeonInternalResource, pluginClassLoader: ClassLoader = this.javaClass.classLoader): InputStream? {
        return pluginClassLoader.getResourceAsStream(neonInternalResource.resourceURL)
    }

    fun verifyResourceChecksum(originalResourceChecksum: String, referenceResourceURL: URL, algorithm: String): Boolean {
        val referenceResourceChecksum = getResourceChecksum(referenceResourceURL, algorithm)

        return originalResourceChecksum.trim() == referenceResourceChecksum
    }

    fun getResourceChecksum(resourceURL: URL, algorithm: String): String {
        val resourceChecksum: String

        resourceURL.openStream().use {
            val fileHashInBytes = MessageDigest.getInstance(algorithm).digest(it.readAllBytes())

            resourceChecksum = BigInteger(1, fileHashInBytes).toString(16)
        }

        return resourceChecksum
    }
}