package com.islandstudio.neon.shared.core.exception

class NeonConnectionException(message: String?): NeonException(
    message ?: "Uncatched NeonConnectionException!"
)