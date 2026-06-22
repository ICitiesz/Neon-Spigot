package com.islandstudio.neon.api.dto.security

data class PermissionDto(
    val id: Long,
    val parentId: Long?,
    val code: String,
    val description: String
)