package com.islandstudio.neon.api.dto.request.player

import java.io.Serializable
import java.util.*

data class CreatePlayerProfileRequestDTO(
    val playerUuid: UUID,
    val playerName: String
): Serializable
