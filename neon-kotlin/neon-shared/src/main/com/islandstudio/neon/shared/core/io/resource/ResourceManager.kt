package com.islandstudio.neon.shared.core.io.resource

import com.islandstudio.neon.shared.PluginAdapter
import com.islandstudio.neon.shared.core.AppContext
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.io.folder.NeonDataFolder
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.inject
import java.io.File
import java.io.InputStream
import java.math.BigInteger
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest

class ResourceManager {
    fun initializeResource() {
        if (!appContext.isVersionCompatible) return

        pluginAdapter.getPluginLogger().info {
            appContext.getCodeMessage("neon.info.resource_manager.data_folder_init")
        }

        NeonDataFolder.reformatVersionFolder()
        NeonDataFolder.getAllDataFolder().forEach { folder ->
            if (folder.exists()) return@forEach

            folder.mkdirs()
        }

        extractExtension()
    }

    companion object: IComponentInjector {
        private val pluginAdapter by inject<PluginAdapter<JavaPlugin>>()
        private val appContext by inject<AppContext>()

        fun extractExtension() {
            pluginAdapter.getPluginLogger().info(appContext.getCodeMessage("neon.info.resource_manager.extension_init"))
            NeonInternalResource.entries
                .filter { it.resourceType == ResourceType.Jar }
                .forEach { extension ->
                    val originalResource = getNeonResourceAsUrl(extension)
                        ?: return@forEach pluginAdapter.plugin.logger.warning(
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
            return pluginAdapter.getPluginClassLoader().getResource(neonInternalResource.resourceURL)
        }

        fun getNeonResourceAsStream(neonInternalResource: NeonInternalResource, pluginClassLoader: ClassLoader? = null): InputStream {
            pluginClassLoader?.let {
                return it.getResourceAsStream(neonInternalResource.resourceURL)
            } ?: return pluginAdapter.getPluginClassLoader().getResourceAsStream(neonInternalResource.resourceURL)
        }
    }


}