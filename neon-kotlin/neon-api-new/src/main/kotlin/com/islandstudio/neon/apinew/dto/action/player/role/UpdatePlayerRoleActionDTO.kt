package com.islandstudio.neon.apinew.dto.action.player.role

data class UpdatePlayerRoleActionDTO (
    val targetCode: String,
    val newDisplayName: String?,
    val newCode: String?
)