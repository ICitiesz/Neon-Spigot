package com.islandstudio.neon.api.dto.player

data class UpdatePlayerRoleDto(
    val targetCode: String,
    val newDisplayName: String?,
    val newCode: String?
)