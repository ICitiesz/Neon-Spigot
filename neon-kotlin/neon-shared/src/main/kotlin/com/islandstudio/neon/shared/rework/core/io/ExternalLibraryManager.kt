package com.islandstudio.neon.shared.rework.core.io

import com.islandstudio.neon.shared.rework.core.initialization.context.IPluginContext
import com.islandstudio.neon.shared.utils.data.DataUtil
import kotlinx.coroutines.*
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.time.Duration.Companion.milliseconds

class ExternalLibraryManager(private val pluginContext: IPluginContext) {
    private val externalLibraryList = ExternalLibrary.getAllExternalLibraries().filter {
        if (it.isRegisterByCondition) {
            it.onRegister()
        } else {
            true
        }
    }

    data class LibraryRegisterEntry(
        val externalLibrary: ExternalLibrary,
        val libraryFile: Deferred<File?>,
        val libraryChecksumFile: Deferred<File?>
    )

    suspend fun registerLibrary(): Array<File> {
        val registredLibraries = ArrayList<File>()

        withContext(Dispatchers.IO) {
            val libraryRegisterEntries = ArrayList<LibraryRegisterEntry>()

            externalLibraryList.forEach { externalLibrary ->
                val libraryFile = File(DataDirectory.NeonLibraryFolder, externalLibrary.getJarFileName())

                /* If library file not exist in local, download both library file and checksum */
                if (!libraryFile.exists()) {
                    libraryRegisterEntries.add(
                         LibraryRegisterEntry(
                             externalLibrary,
                             async {
                                 tryDownloadFile(externalLibrary.getLibraryURL(), libraryFile)
                             },
                             async {
                                 tryDownloadChecksumFile(externalLibrary)
                             }
                         )
                    )

                    return@forEach
                }

                libraryRegisterEntries.add(
                    LibraryRegisterEntry(
                        externalLibrary,
                        CompletableDeferred(libraryFile),
                        async {
                            tryDownloadChecksumFile(externalLibrary)
                        }
                    )
                )
            }

            libraryRegisterEntries.forEach { libraryRegisterEntry ->
                val libraryFile = libraryRegisterEntry.libraryFile.await()
                val libraryChecksumFile = libraryRegisterEntry.libraryChecksumFile.await()

                pluginContext.getPluginLogger().info { "Registering external library ${libraryRegisterEntry.externalLibrary.getJarFileName()} ......" }

                if (libraryFile == null || !libraryFile.exists()) {
                    pluginContext.getPluginLogger().severe { "Failed to register external library ${libraryRegisterEntry.externalLibrary.getJarFileName()}: Missing library file!" }
                    return@forEach
                }

                if (libraryChecksumFile == null || !libraryChecksumFile.exists()) {
                    pluginContext.getPluginLogger().severe { "Failed to register external library ${libraryRegisterEntry.externalLibrary.getJarFileName()}: Missing checksum file!" }
                    return@forEach
                }

                /* Validate file integrity */
                val isValidFileIntegrity = runCatching {
                    pluginContext.getPluginLogger().info { "Verifying integrity of ${libraryRegisterEntry.externalLibrary.getJarFileName()} ......" }

                    pluginContext.resourceManager.verifyResourceChecksum(
                        libraryChecksumFile.readText(),
                        libraryFile.toURI().toURL(),
                        libraryChecksumFile.extension.uppercase()
                    )
                }.getOrElse {
                    pluginContext.getPluginLogger().severe { "Failed to verify integrity of ${libraryRegisterEntry.externalLibrary.getJarFileName()}!" }
                    false
                }

                if (!isValidFileIntegrity) {
                    pluginContext.getPluginLogger().severe { "Failed to register external library ${libraryRegisterEntry.externalLibrary.getJarFileName()}: External library file may be corrupted!" }
                    return@forEach
                }

                registredLibraries.add(libraryFile)
            }

            registredLibraries.add(pluginContext.mainPluginFile)
        }

        return registredLibraries.toTypedArray()
    }

    private suspend fun tryDownloadChecksumFile(externalLibrary: ExternalLibrary): File? {
        val librarySHA256ChecksumFile = File(DataDirectory.NeonLibraryFolder, externalLibrary.getSHA256ChecksumFileName())
        val librarySHA1ChecksumFile = File(DataDirectory.NeonLibraryFolder, externalLibrary.getSHA1ChecksumFileName())

        return when {
            librarySHA256ChecksumFile.exists() -> librarySHA256ChecksumFile

            librarySHA1ChecksumFile.exists() -> librarySHA1ChecksumFile

            else -> {
                tryDownloadFile(externalLibrary.getSHA256ChecksumURL(), librarySHA256ChecksumFile)?.let {
                    return it
                }

                tryDownloadFile(externalLibrary.getSHA1ChecksumURL(), librarySHA1ChecksumFile)?.let {
                    return it
                }
            }
        }
    }

    private suspend fun tryDownloadFile(fileURL: URL, file: File, isOverwrite: Boolean = false, maxRetry: Int = 3): File? {
        return withContext(Dispatchers.IO) {
            repeat(maxRetry) { retryCount ->
                val attemptCount = retryCount + 1

                pluginContext.getPluginLogger().info { "Downloading ${file.name} ......" }

                try {
                    /* Try to establish connection for the file URL */
                    val fileURLConnection = DataUtil.asType<HttpURLConnection>(fileURL.openConnection()).apply {
                        this.connectTimeout = 5000
                        this.readTimeout = 5000
                    }

                    /* Filter any response not equal status 200 */
                    if (fileURLConnection.responseCode != HttpURLConnection.HTTP_OK) {
                        when(fileURLConnection.responseCode) {
                            /* Return false if not found */
                            HttpURLConnection.HTTP_NOT_FOUND -> {
                                pluginContext.getPluginLogger().severe { "File ${file.name} not found!" }
                                return@withContext null
                            }

                            /* Retry */
                            else -> {
                                if (!canRetryDownload(attemptCount, maxRetry, file.name)) return@withContext null

                                return@repeat
                            }
                        }
                    }

                    /* Copy the file to local */
                    fileURLConnection.inputStream.use {
                        if (isOverwrite) {
                            Files.copy(it, file.toPath(), StandardCopyOption.REPLACE_EXISTING)
                            return@use
                        }

                        Files.copy(it, file.toPath())
                    }

                    return@withContext file
                } catch (ex: Exception) {
                    if (!canRetryDownload(attemptCount, maxRetry, file.name)) {
                        pluginContext.getPluginLogger().severe { ex.message }
                        return@withContext null
                    }

                    pluginContext.getPluginLogger().warning {
                        "Failed to download ${file.name} in attempt $attemptCount! Retrying....."
                    }
                    delay((1000 * (attemptCount).toLong()).milliseconds)
                }
            }

            return@withContext null
        }
    }

    private fun canRetryDownload(attemptCount: Int, maxRetry: Int, fileName: String): Boolean {
        if (attemptCount <= maxRetry) return true

        pluginContext.getPluginLogger().severe { "Failed to download $fileName after $maxRetry attempt!" }
        return false
    }
}