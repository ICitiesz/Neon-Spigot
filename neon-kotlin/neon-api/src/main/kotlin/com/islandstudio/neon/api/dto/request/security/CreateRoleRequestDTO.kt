package com.islandstudio.neon.api.dto.request.security

import com.islandstudio.neon.api.entity.security.RoleEntity
import java.io.Serializable

data class CreateRoleRequestDTO(
    val roleEntity: RoleEntity
): Serializable