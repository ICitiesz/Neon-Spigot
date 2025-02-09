package com.islandstudio.neon.api.dto.request.security.permission

import java.io.Serializable

data class GrantRolePermissionRequestDTO(
    val roleId: Long,
    val permissionId: Long,
    val parentRolePermissionId: Long?
): Serializable