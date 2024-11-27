package com.islandstudio.neon.stable.core.io.resource

import com.islandstudio.neon.Neon
import com.islandstudio.neon.stable.core.application.AppContext
import com.islandstudio.neon.stable.core.application.di.ModuleInjector
import com.islandstudio.neon.stable.core.application.extension.NeonExtensions
import com.islandstudio.neon.stable.core.database.DatabaseConnector.neon
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

class ResourceManager: ModuleInjector {
    private val neon by inject<Neon>()
    private val appContext by inject<AppContext>()

    companion object: ModuleInjector {
        private val appContext by inject<AppContext>()

        fun run() {
            if (!appContext.isVersionCompatible) return

            CoroutineScope(Dispatchers.IO).launch {
                neon.logger.info(appContext.getCodeMessage("neon.info.resource_manager.data_folder_init"))

                reformatVersionFolder()

                NeonDataFolder.getAllDataFolder().forEach { folder ->
                    if (folder.exists()) return@forEach

                    folder.mkdirs()
                }
            }.invokeOnCompletion {
                ResourceManager().extractExtension()
            }
        }

        /**
         * Get the root data folder of Neon which inside the plugin directory within the server directory.
         * @return The root data folder of Neon. [File]
         */
        fun getRootDataFolder(): File {
            return neon.dataFolder.apply {
                if (!this.exists()) this.mkdirs()
            }
        }

        /**
         * Reformat version folder from older formart, '1_17' to new format '1.17'
         *
         */
        private fun reformatVersionFolder() {
            /* Old version format: '1_17'
            * New version format: '1.17' */
            getRootDataFolder().listFiles()?.let {
                it.filter { folder ->
                    folder.isDirectory && folder.name.matches("^\\d_\\d\\d\$".toRegex())
                }.forEach { folder ->
                    folder.renameTo(File(getRootDataFolder(), folder.name.replace("_", ".")))
                }
            }

        }
    }

    fun extractExtension() {
        CoroutineScope(Dispatchers.IO).launch {
            neon.logger.info(appContext.getCodeMessage("neon.info.resource_manager.extension_init"))
            NeonResources.entries
                .filter { it.resourceType == ResourceType.JAR }
                .forEach { extension ->
                    val originalResource = getNeonResourceAsUrl(extension)
                        ?: return@forEach neon.logger.warning(
                            appContext.getFormattedCodeMessage("neon.info.resource_manager.extension_extract_failed",
                                extension.getResourceName()))
                        //?: return@forEach neon.logger.warning("Missing extension! Skipping extension extration for '${extension.getResourceName()}'...")
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
        }.invokeOnCompletion {
            neon.getAppInitializer().loadExtension(NeonExtensions.NeonDatabaseExtension())
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

    fun getNeonResourceAsUrl(neonResource: NeonResources): URL? {
        return neon.getPluginClassLoader().getResource(neonResource.resourcePath)
    }

    fun getNeonResourceAsStream(neonResource: NeonResources, pluginClassLoader: ClassLoader? = null): InputStream {
        pluginClassLoader?.let {
            return it.getResourceAsStream(neonResource.resourcePath)
        } ?: return neon.getPluginClassLoader().getResourceAsStream(neonResource.resourcePath)
    }
}