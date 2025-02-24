package com.islandstudio.neon.api.dto.request.security.permission

import java.io.Serializable

data class BatchRevokeRolePermissionRequestDTO(
    val rolePermissionList: List<RevokeRolePermissionRequestDTO>
): Serializable
