package com.islandstudio.neon.apinew.dto.action

import java.time.LocalDateTime
import java.util.*

data class CreatePlayerProfileActionDTO(
    val uuid: UUID,
    val name: String,
    val joinedAt: LocalDateTime,
    val roleId: Long?
)
