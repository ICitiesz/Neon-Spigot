package com.islandstudio.neon.api.dto.request.player

import java.io.Serializable
import java.util.*

data class GetPlayerProfileRequestDTO(
    val playerUuid: UUID? = null,
    val playerName: String? = null,
): Serializable
