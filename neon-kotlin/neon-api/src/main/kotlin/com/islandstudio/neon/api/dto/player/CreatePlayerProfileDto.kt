package com.islandstudio.neon.api.dto.player

import java.util.*

data class CreatePlayerProfileDto(
    val uuid: UUID,
    val name: String
)