package com.islandstudio.neon.api.dto.request.security.permission

import java.io.Serializable

data class BatchGrantRolePermissionRequestDTO(
    val rolePermissionList: List<GrantRolePermissionRequestDTO>
): Serializable
