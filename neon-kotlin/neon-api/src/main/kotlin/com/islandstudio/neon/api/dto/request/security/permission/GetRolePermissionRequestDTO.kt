package com.islandstudio.neon.api.dto.request.security.permission

import java.io.Serializable

data class GetRolePermissionRequestDTO(
    val rolePermissionId: Long? = null,
    val roleId: Long? = null,
    val permissionId: Long? = null,
    val parentRolePermissionId: Long? = null,
    val includePermissionCode: Boolean = false
): Serializable