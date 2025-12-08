package com.islandstudio.neon.shared.core.io.resource.library

import com.islandstudio.neon.shared.core.exception.NeonException
import com.islandstudio.neon.shared.core.initialization.IPluginContext
import com.islandstudio.neon.shared.core.io.resource.NeonInternalResource
import com.islandstudio.neon.shared.experimental.NeonDataFolderNew
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class NeonLibraryManager(private val pluginContext: IPluginContext) {
    private val neonLibraryList: ArrayList<NeonLibrary> = run {
        val neonLibsJsonBufferedReader = pluginContext.resourceManager.getNeonResourceAsStream(NeonInternalResource.NeonLibraryList)
            ?.bufferedReader() ?: throw NeonException("Could not get library list")

        return@run neonLibsJsonBufferedReader.use {
            return@use Json.decodeFromString<ArrayList<NeonLibrary>>(it.readText())
        }
    }

    suspend fun getLibraries(): ArrayList<File> {
        return withContext(Dispatchers.IO) {
            val libraryFiles = ArrayList<File>()

            neonLibraryList.forEach { neonLibrary ->
                val libraryJarFile = File(NeonDataFolderNew.NeonLibraryFolder, neonLibrary.getLibraryFileName())
                val libraryChecksumFile = File(NeonDataFolderNew.NeonLibraryFolder, neonLibrary.getChecksumFileName())

                /* Library initialization (download) */
                if (!initializeLibrary(neonLibrary)) return@forEach

                /* Integrity Validation  */
                val isSameIntegrity = runCatching {
                    pluginContext.resourceManager.verifyResourceChecksum(libraryChecksumFile.readText(), libraryJarFile.toURI().toURL())
                }.getOrElse { false }

                if (!isSameIntegrity) {
                    if (!downloadFile(neonLibrary.getLibraryURL(), libraryJarFile)) {
                        pluginContext.getPluginLogger().severe { "Failed to load library: ${neonLibrary.name}" }
                        return@forEach
                    }
                }

                libraryFiles.add(libraryJarFile)
            }

            libraryFiles.add(pluginContext.mainPluginFile)
            pluginContext.getPluginLogger().info("Library initialization completed!")

            libraryFiles
        }
    }

    private suspend fun initializeLibrary(neonLibrary: NeonLibrary): Boolean {
        return withContext(Dispatchers.IO) {
            val libraryJarFile = File(NeonDataFolderNew.NeonLibraryFolder, neonLibrary.getLibraryFileName())
            val libraryChecksumFile = File(NeonDataFolderNew.NeonLibraryFolder, neonLibrary.getChecksumFileName())

            if (!libraryJarFile.exists()) {
                if (!downloadFile(neonLibrary.getLibraryURL(), libraryJarFile)) {
                    pluginContext.getPluginLogger().severe { "Failed to load library: ${neonLibrary.name}" }
                    return@withContext false
                }
            }

            if (!libraryChecksumFile.exists()) {
                if (!downloadFile(neonLibrary.getChecksumURL(), libraryChecksumFile)) {
                    pluginContext.getPluginLogger().severe { "Failed to load library: ${neonLibrary.name}" }
                    return@withContext false
                }
            }

            return@withContext true
        }
    }

    private suspend fun downloadFile(fileURL: URL, file: File, isOverwrite: Boolean = false): Boolean {
        return withContext(Dispatchers.IO) {
            val maxRetry = 3

            repeat(maxRetry) { tryCount ->
                runCatching {
                    fileURL.openStream().use {
                        pluginContext.getPluginLogger().info { "Downloading ${file.name} ......" }
                        if (isOverwrite) {
                            Files.copy(it, file.toPath(), StandardCopyOption.REPLACE_EXISTING)
                            return@use
                        }

                        Files.copy(it, file.toPath())
                    }

                    return@withContext true
                }.onFailure {
                    it.printStackTrace()

                    if (tryCount == maxRetry - 1) {
                        pluginContext.getPluginLogger().severe { "Failed to download ${file.name} after $maxRetry attempt!" }
                        return@withContext false
                    }

                    val attemptCount = tryCount + 1

                    pluginContext.getPluginLogger().warning { "Failed to download ${file.name} in attempt $attemptCount! Retrying....." }
                    delay(1000 * (attemptCount).toLong())
                }
            }

            return@withContext false
        }
    }
}