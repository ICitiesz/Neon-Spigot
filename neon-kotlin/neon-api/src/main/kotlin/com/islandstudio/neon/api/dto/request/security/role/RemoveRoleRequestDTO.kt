package com.islandstudio.neon.api.dto.request.security.role

import java.io.Serializable

data class RemoveRoleRequestDTO(
    val roleCode: String
): Serializable
