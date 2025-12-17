package com.islandstudio.neon.shared.core.io.resource.library

import oshi.PlatformEnum
import oshi.SystemInfo
import java.net.URL

sealed class NeonLibrary: INeonLibrary {
    companion object {
        fun getAllLibraries(): ArrayList<NeonLibrary> {
            return NeonLibrary::class.sealedSubclasses
                .map {
                    it.objectInstance as NeonLibrary
                }.toCollection(ArrayList())
        }
    }

    data object KotlinCSVLibrary: NeonLibrary() {
        override val name: String = "Kotlin CSV"
        override val version: String = "1.10.0"
        override val mavenUrl: String = "https://repo1.maven.org/maven2/com/jsoizo/kotlin-csv-jvm/1.10.0/kotlin-csv-jvm-1.10.0.jar"
        override val checksum: Map<ChecksumType, String> = mapOf(
            ChecksumType.MD5 to "https://repo1.maven.org/maven2/com/jsoizo/kotlin-csv-jvm/1.10.0/kotlin-csv-jvm-1.10.0.jar.md5",
        )
        override val requiredFor: ArrayList<String> = arrayListOf("Neon")
        override val isLoadByCondition: Boolean = false
    }

//    data object SQLiteJDBCLibrary: NeonLibrary() {
//        override val name: String = "SQLite JDBC"
//        override val version: String = "3.51.1.0"
//        override val mavenUrl: String = "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.51.1.0/sqlite-jdbc-3.51.1.0.jar"
//        override val checksum: Map<ChecksumType, String> = mapOf(
//            ChecksumType.MD5 to "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.51.1.0/sqlite-jdbc-3.51.1.0.jar.md5",
//        )
//        override val requiredFor: ArrayList<String> = arrayListOf()
//        override val isLoadByCondition: Boolean = false
//    }

    data object MariaDB4jDBWinx64Library: NeonLibrary() {
        override val name: String = "MariaDB4j DB Winx64"
        override val version: String = "11.4.5"
        override val mavenUrl: String = "https://repo1.maven.org/maven2/ch/vorburger/mariaDB4j/mariaDB4j-db-winx64/11.4.5/mariaDB4j-db-winx64-11.4.5.jar"
        override val checksum: Map<ChecksumType, String> = mapOf(
            ChecksumType.MD5 to "https://repo1.maven.org/maven2/ch/vorburger/mariaDB4j/mariaDB4j-db-winx64/11.4.5/mariaDB4j-db-winx64-11.4.5.jar.md5",
        )
        override val requiredFor: ArrayList<String> = arrayListOf()
        override val isLoadByCondition: Boolean = true
        override fun onLoad(): Boolean {
            return SystemInfo.getCurrentPlatform() == PlatformEnum.WINDOWS && SystemInfo().operatingSystem.bitness == 64
        }
    }

    data object MariaDB4jDBLinux64Library: NeonLibrary() {
        override val name: String = "MariaDB4j DB Linux64"
        override val version: String = "11.4.5"
        override val mavenUrl: String = "https://repo1.maven.org/maven2/ch/vorburger/mariaDB4j/mariaDB4j-db-linux64/11.4.5/mariaDB4j-db-linux64-11.4.5.jar"
        override val checksum: Map<ChecksumType, String> = mapOf(
            ChecksumType.MD5 to "https://repo1.maven.org/maven2/ch/vorburger/mariaDB4j/mariaDB4j-db-linux64/11.4.5/mariaDB4j-db-linux64-11.4.5.jar.md5",
        )
        override val requiredFor: ArrayList<String> = arrayListOf()
        override val isLoadByCondition: Boolean = true

        override fun onLoad(): Boolean {
            return SystemInfo.getCurrentPlatform() == PlatformEnum.LINUX && SystemInfo().operatingSystem.bitness == 64
        }
    }

    data object MariaDB4jMacOSARM64Library: NeonLibrary() {
        override val name: String = "MariaDB4j DB MacOS ARM64"
        override val version: String = "11.4.5"
        override val mavenUrl: String = "https://repo1.maven.org/maven2/ch/vorburger/mariaDB4j/mariaDB4j-db-macos-arm64/11.4.5/mariaDB4j-db-macos-arm64-11.4.5.jar"
        override val checksum: Map<ChecksumType, String> = mapOf(
            ChecksumType.MD5 to "https://repo1.maven.org/maven2/ch/vorburger/mariaDB4j/mariaDB4j-db-macos-arm64/11.4.5/mariaDB4j-db-macos-arm64-11.4.5.jar.md5",
        )
        override val requiredFor: ArrayList<String> = arrayListOf()
        override val isLoadByCondition: Boolean = true

        override fun onLoad(): Boolean {
            return SystemInfo.getCurrentPlatform() == PlatformEnum.MACOS
        }
    }

//    data object ModelMapperLibrary: NeonLibrary() {
//        override val name: String = "ModelMapper "
//        override val version: String = "3.2.6"
//        override val mavenUrl: String = "https://repo1.maven.org/maven2/org/modelmapper/modelmapper/3.2.6/modelmapper-3.2.6.jar"
//        override val checksum: Map<ChecksumType, String> = mapOf(
//            ChecksumType.MD5 to "https://repo1.maven.org/maven2/org/modelmapper/modelmapper/3.2.6/modelmapper-3.2.6.jar.md5",
//        )
//        override val requiredFor: ArrayList<String> = arrayListOf()
//        override val isLoadByCondition: Boolean = false
//    }
}

private interface INeonLibrary {
    val name: String
    val version: String
    val mavenUrl: String
    val checksum: Map<ChecksumType, String>
    val requiredFor: ArrayList<String>
    val isLoadByCondition: Boolean

    fun onLoad(): Boolean {
        return !isLoadByCondition
    }

    fun getLibraryURL(): URL {
        return URL(mavenUrl)
    }

    fun getChecksumURL(): URL {
        return URL(checksum[ChecksumType.MD5])
    }

    fun getLibraryFileName(): String {
        return getLibraryURL().path.substringAfterLast('/')
    }

    fun getChecksumFileName(): String {
        return getChecksumURL().path.substringAfterLast('/')
    }
}
