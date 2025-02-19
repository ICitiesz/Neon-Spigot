package com.islandstudio.neon.api.dto.response.security

import com.islandstudio.neon.api.entity.security.RoleEntity
import java.io.Serializable

data class RoleListResponseDTO(
    val roleList: List<RoleEntity>
): Serializable
