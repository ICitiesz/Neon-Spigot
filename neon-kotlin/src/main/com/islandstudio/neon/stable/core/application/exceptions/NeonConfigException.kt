package com.islandstudio.neon.stable.core.application.exceptions

class NeonConfigException(message: String = "Unhandled neon config exception!", externalCauseBy: Class<Any>? = null): NeonException(
    externalCauseBy?.let { "$message | External cause by: ${externalCauseBy.canonicalName}" } ?: message
)