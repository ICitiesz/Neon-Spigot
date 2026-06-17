package com.islandstudio.neon.shared.rework.core.io

import java.net.URL

internal interface IExternalLibrary {
    companion object {
        private const val MAVEN_REPO_BASE_URL: String = "https://repo1.maven.org/maven2/"
    }

    val name: String
    val groupId: String
    val artifactId: String
    val version: String
    val requiredFor: Array<String>
    val isLoadByCondition: Boolean

    fun onLoad(): Boolean {
        return !isLoadByCondition
    }

    fun getLibraryBaseRepoURL(): URL {
        val groupPath = groupId.replace(".", "/")

        return URL("$MAVEN_REPO_BASE_URL$groupPath/$artifactId/$version")
    }

    fun getLibraryURL(): URL {
        return URL("${getLibraryBaseRepoURL()}/${getJarFileName()}")
    }

    fun getSHA1ChecksumURL(): URL {
        return URL("${getLibraryBaseRepoURL()}/${getSHA1ChecksumFileName()}")
    }

    fun getSHA256ChecksumURL(): URL {
        return URL("${getLibraryBaseRepoURL()}/${getSHA256ChecksumFileName()}")
    }

    fun getJarFileName(): String {
        return "${artifactId}-${version}.jar"
    }

    fun getSHA1ChecksumFileName(): String {
        return "${getJarFileName()}.sha1"
    }

    fun getSHA256ChecksumFileName(): String {
        return "${getJarFileName()}.sha256"
    }
}