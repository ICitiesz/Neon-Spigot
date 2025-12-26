package com.islandstudio.neon.apinew.dto.result.player.permission

data class RolePermissionDetailResultDTO(
    val id: Long,
    val parentId: Long?,
    val roleId: Long,
    val roleCode: String,
    val permissionId: Long,
    val permissionCode: String,
)
