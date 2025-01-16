package com.islandstudio.neon.shared.core.exception

class NeonConfigException(message: String = "Unhandled neon config exception!", externalCauseBy: Class<Any>? = null): NeonException(
    externalCauseBy?.let { "$message | External cause by: ${externalCauseBy.canonicalName}" } ?: message
)