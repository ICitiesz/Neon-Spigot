package com.islandstudio.neon.shared.core.io.resource.library

import kotlinx.serialization.Serializable
import java.net.URL

@Serializable
data class NeonLibrary(
    val name: String,
    val version: String,
    val mavenUrl: String,
    val checksum: Map<String, String>,
    val requiredFor: ArrayList<String>
) {
    fun getLibraryURL(): URL {
        return URL(mavenUrl)
    }

    fun getChecksumURL(): URL {
        return URL(checksum[ChecksumType.MD5.value])
    }

    fun getLibraryFileName(): String {
        return getLibraryURL().path.substringAfterLast('/')
    }

    fun getChecksumFileName(): String {
        return getChecksumURL().path.substringAfterLast('/')
    }
}
