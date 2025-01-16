package com.islandstudio.neon.shared.core.exception

class NeonLoaderException(message: String?): NeonException(
    message ?: "Uncatched NeonLoaderException!"
)