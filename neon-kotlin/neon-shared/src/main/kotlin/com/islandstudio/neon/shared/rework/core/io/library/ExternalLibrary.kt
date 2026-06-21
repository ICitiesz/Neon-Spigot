package com.islandstudio.neon.shared.rework.core.io.library

import oshi.SystemInfo
import oshi.util.PlatformEnum

sealed class ExternalLibrary(override val name: String): IExternalLibrary {
    companion object {
        fun getAllExternalLibraries(): ArrayList<ExternalLibrary> {
            return ExternalLibrary::class.sealedSubclasses
                .map {
                    it.objectInstance as ExternalLibrary
                }.toCollection(ArrayList())
        }
    }

    data object KotlinCSVLibrary: ExternalLibrary("Kotlin CSV") {
        override val groupId: String = "com.jsoizo"
        override val artifactId: String = "kotlin-csv-jvm"
        override val version: String = "1.10.0"
        override val requiredFor: Array<String> = arrayOf("Neon")
        override val isRegisterByCondition: Boolean = false
    }

    data object MariaDB4jDBWinx64Library: ExternalLibrary("MariaDB4j DB Winx64") {
        override val groupId: String = "ch.vorburger.mariaDB4j"
        override val artifactId: String = "mariaDB4j-db-winx64"
        override val version: String = "11.4.5"
        override val requiredFor: Array<String> = emptyArray()
        override val isRegisterByCondition: Boolean = true

        override fun onRegister(): Boolean {
            return PlatformEnum.getCurrentPlatform() == PlatformEnum.WINDOWS && SystemInfo().operatingSystem.bitness == 64
        }
    }

    data object MariaDB4jDBLinux64Library: ExternalLibrary("MariaDB4j DB Linux64") {
        override val groupId: String = "ch.vorburger.mariaDB4j"
        override val artifactId: String = "mariaDB4j-db-linux64"
        override val version: String = "11.4.5"
        override val requiredFor: Array<String> = emptyArray()
        override val isRegisterByCondition: Boolean = true

        override fun onRegister(): Boolean {
            return PlatformEnum.getCurrentPlatform() == PlatformEnum.LINUX && SystemInfo().operatingSystem.bitness == 64
        }
    }

    data object MariaDB4jMacOSARM64Library: ExternalLibrary("MariaDB4j DB MacOS ARM64") {
        override val groupId: String = "ch.vorburger.mariaDB4j"
        override val artifactId: String = "mariaDB4j-db-macos-arm64"
        override val version: String = "11.4.5"
        override val requiredFor: Array<String> = emptyArray()
        override val isRegisterByCondition: Boolean = true

        override fun onRegister(): Boolean {
            return PlatformEnum.getCurrentPlatform() == PlatformEnum.MACOS && SystemInfo().operatingSystem.bitness == 64
        }
    }

    // Maybe deprecated
//    data object ModelMapperLibrary: ExternalLibrary("ModelMapper") {
//        override val groupId: String = "org.modelmapper"
//        override val artifactId: String = "modelmapper"
//        override val version: String = "3.2.6"
//        override val requiredFor: Array<String> = emptyArray()
//        override val isRegisterByCondition: Boolean = false
//    }

    // Maybe deprecated
//    data object SQLiteJDBCLibrary: ExternalLibrary("SQLite JDBC") {
//        override val groupId: String = "org.xerial"
//        override val artifactId: String = "sqlite-jdbc"
//        override val version: String = "3.51.1.0"
//        override val requiredFor: Array<String> = emptyArray()
//        override val isRegisterByCondition: Boolean = false
//    }
}