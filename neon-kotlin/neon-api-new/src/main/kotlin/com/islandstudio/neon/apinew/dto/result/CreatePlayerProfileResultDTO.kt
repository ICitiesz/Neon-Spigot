package com.islandstudio.neon.apinew.dto.result

import java.util.*

data class CreatePlayerProfileResultDTO(
    val uuid: UUID,
    val name: String,
    val roleId: Long?
)