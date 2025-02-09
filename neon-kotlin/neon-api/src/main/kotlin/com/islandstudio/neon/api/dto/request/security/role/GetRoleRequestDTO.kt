package com.islandstudio.neon.api.dto.request.security.role

import java.io.Serializable

data class GetRoleRequestDTO(
    val roleId: Long?,
    val roleCode: String?
): Serializable
