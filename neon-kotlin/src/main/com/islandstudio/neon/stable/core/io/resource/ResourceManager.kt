package com.islandstudio.neon.stable.core.io.resource

import com.islandstudio.neon.Neon
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.stable.core.application.AppContext
import com.islandstudio.neon.stable.core.io.nFile.NeonDataFolder
import org.koin.core.component.inject
import java.io.File
import java.io.InputStream
import java.math.BigInteger
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest

class ResourceManager: IComponentInjector {
    private val neon by inject<Neon>()
    private val appContext by inject<AppContext>()

    companion object: IComponentInjector {
        private val neon by inject<Neon>()
        private val appContext by inject<AppContext>()

        fun run() {
            if (!appContext.isVersionCompatible) return

            neon.logger.info(appContext.getCodeMessage("neon.info.resource_manager.data_folder_init"))

            NeonDataFolder.reformatVersionFolder()
            NeonDataFolder.getAllDataFolder().forEach { folder ->
                if (folder.exists()) return@forEach

                folder.mkdirs()
            }

            ResourceManager().extractExtension()
        }
    }

    fun extractExtension() {
        neon.logger.info(appContext.getCodeMessage("neon.info.resource_manager.extension_init"))
        NeonInternalResource.entries
            .filter { it.resourceType == ResourceType.Jar }
            .forEach { extension ->
                val originalResource = getNeonResourceAsUrl(extension)
                    ?: return@forEach neon.logger.warning(
                        appContext.getFormattedCodeMessage(
                            "neon.info.resource_manager.extension_extract_failed",
                            extension.getResourceName()
                        )
                    )
                val extensionFile = File(NeonDataFolder.ExtensionFolder, extension.getResourceName())

                if (!extensionFile.exists()) {
                    copyResource(originalResource, extensionFile)
                    return@forEach
                }

                if (verifyResourceCheckSum(originalResource, extensionFile.toURI().toURL())) {
                    return@forEach
                }

                copyResource(originalResource, extensionFile)
            }

        //neon.getAppInitializer().loadExtension(NeonExtensions.NeonDatabaseExtension)
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

    fun getNeonResourceAsUrl(neonInternalResource: NeonInternalResource): URL? {
        return neon.getPluginClassLoader().getResource(neonInternalResource.resourceURL)
    }

    fun getNeonResourceAsStream(neonInternalResource: NeonInternalResource, pluginClassLoader: ClassLoader? = null): InputStream {
        pluginClassLoader?.let {
            return it.getResourceAsStream(neonInternalResource.resourceURL)
        } ?: return neon.getPluginClassLoader().getResourceAsStream(neonInternalResource.resourceURL)
    }
}