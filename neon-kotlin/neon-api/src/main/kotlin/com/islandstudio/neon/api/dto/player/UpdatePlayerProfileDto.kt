package com.islandstudio.neon.api.dto.player

data class UpdatePlayerProfileDto(
    val id: Long,
    val uuid: String,
    val name: String
)