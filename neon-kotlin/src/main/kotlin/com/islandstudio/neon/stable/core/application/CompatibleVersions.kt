package com.islandstudio.neon.stable.core.application

enum class CompatibleVersions(vararg val versions: String) {
    V1_17("1.17", "1.17.1"),
    V1_18("1.18", "1.18.1", "1.18.2"),
    V1_19("1.19", "1.19.1", "1.19.2", "1.19.3", "1.19.4"),
    V1_20("1.20", "1.20.1", "1.20.2", "1.20.3", "1.20.4");

    fun getMajorVersion(): String = versions.first()
}