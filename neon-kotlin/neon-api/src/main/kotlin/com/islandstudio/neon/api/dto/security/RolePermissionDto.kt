package com.islandstudio.neon.api.dto.security

data class RolePermissionDto(
    val id: Long,
    val parentId: Long?,
    val roleId: Long,
    val permissionId: Long
)
