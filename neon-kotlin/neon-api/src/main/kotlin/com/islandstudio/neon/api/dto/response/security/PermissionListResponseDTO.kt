package com.islandstudio.neon.api.dto.response.security

import com.islandstudio.neon.api.entity.security.PermissionEntity
import java.io.Serializable

data class PermissionListResponseDTO(
    val permissionList: List<PermissionEntity>
): Serializable
