package com.islandstudio.neon.api.dto.request.security.permission

import java.io.Serializable

data class GetRolePermissionRequestDTO(
    val rolePermissionId: Long?,
    val roleId: Long?,
    val permissionId: Long?,
    val parentRolePermissionId: Long?
): Serializable