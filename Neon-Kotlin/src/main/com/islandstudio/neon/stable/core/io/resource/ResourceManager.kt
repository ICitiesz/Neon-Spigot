package com.islandstudio.neon.stable.core.io.resource

import com.islandstudio.neon.Neon
import com.islandstudio.neon.stable.core.application.AppContext
import com.islandstudio.neon.stable.core.io.nFile.NeonDataFolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import java.io.File
import java.io.InputStream
import java.math.BigInteger
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest

class ResourceManager: AppContext.Injector {
    private val neon by inject<Neon>()

    fun extractExtension() {
        CoroutineScope(Dispatchers.IO).launch {
            NeonResources.entries
                .filter { it.resourceType == ResourceType.JAR }
                .forEach { extension ->
                    val originalResource = neon.getPluginClassLoader().getResource(extension.resourcePath)
                        ?: return@forEach neon.logger.warning("Missing extension! Skipping extension extration for '${extension.getResourceName()}'...")
                    val extensionFile = File(NeonDataFolder.ExtensionFolder(), extension.getResourceName())

                    if (!extensionFile.exists()) {
                        copyResource(originalResource, extensionFile)
                        return@forEach
                    }

                    if (verifyResourceCheckSum(originalResource, extensionFile.toURI().toURL())) {
                        return@forEach
                    }

                    copyResource(originalResource, extensionFile)
                }
        }
    }

    fun verifyResourceCheckSum(originalResource: URL, destinationResource: URL): Boolean {
        val originalResourceFileHash = with(originalResource.openStream()) {
            return@with this.use {
                val fileHashInBytes = MessageDigest.getInstance("MD5").digest(this.readAllBytes())

                BigInteger(1, fileHashInBytes).toString(16)
            }
        }

        val destinationResourceFileHash = with(destinationResource.openStream()) {
            return@with this.use {
                val fileHashInBytes = MessageDigest.getInstance("MD5").digest(this.readAllBytes())

                BigInteger(1, fileHashInBytes).toString(16)
            }
        }

        return destinationResourceFileHash == originalResourceFileHash
    }

    fun copyResource(originalResource: URL, destinationResource: File) {
        originalResource.openStream().use {
            Files.copy(it, destinationResource.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
    }

    fun getNeonResource(neonResource: NeonResources): URL? {
        return neon.getPluginClassLoader().getResource(neonResource.resourcePath)
    }

    fun getNeonResourceAsStream(neonResource: NeonResources, pluginClassLoader: ClassLoader? = null): InputStream? {
        pluginClassLoader?.let {
            return it.getResourceAsStream(neonResource.resourcePath)
        } ?: return neon.getPluginClassLoader().getResourceAsStream(neonResource.resourcePath)
    }
}