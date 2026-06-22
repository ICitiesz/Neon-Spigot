package com.islandstudio.neon.api.dto.player

import java.time.LocalDateTime

data class PlayerProfileDto(
    val id: Long,
    val uuid: String,
    val name: String,
    val joinedAt: LocalDateTime,
    val roleId: Long?
)