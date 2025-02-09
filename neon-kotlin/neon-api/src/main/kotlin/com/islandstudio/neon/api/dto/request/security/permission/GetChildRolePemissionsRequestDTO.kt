package com.islandstudio.neon.api.dto.request.security.permission

import java.io.Serializable

data class GetChildRolePemissionsRequestDTO(
    val parentRolePermissionId: Long
): Serializable