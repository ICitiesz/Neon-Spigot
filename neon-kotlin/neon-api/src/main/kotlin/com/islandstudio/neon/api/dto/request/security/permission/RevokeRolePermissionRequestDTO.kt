package com.islandstudio.neon.api.dto.request.security.permission

import java.io.Serializable

data class RevokeRolePermissionRequestDTO(
    val rolePermissionId: Long?,
    val roleId: Long?
): Serializable
