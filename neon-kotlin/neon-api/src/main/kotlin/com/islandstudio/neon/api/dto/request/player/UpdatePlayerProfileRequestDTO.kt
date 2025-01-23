package com.islandstudio.neon.api.dto.request.player

import java.io.Serializable
import java.util.*

data class UpdatePlayerProfileRequestDTO(
    val playerUUID: UUID,
    val playerName: String
): Serializable
