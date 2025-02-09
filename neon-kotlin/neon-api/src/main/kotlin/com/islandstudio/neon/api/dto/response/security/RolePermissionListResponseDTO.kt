package com.islandstudio.neon.api.dto.response.security

import com.islandstudio.neon.api.entity.security.RolePermissionEntity
import java.io.Serializable

data class RolePermissionListResponseDTO(
    val rolePermissionList: List<RolePermissionEntity>
): Serializable