package com.islandstudio.neon.shared.rework.core.io

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
            return false
            //return SystemInfo.getCurrentPlatform() == PlatformEnum.WINDOWS && SystemInfo().operatingSystem.bitness == 64
        }
    }
}