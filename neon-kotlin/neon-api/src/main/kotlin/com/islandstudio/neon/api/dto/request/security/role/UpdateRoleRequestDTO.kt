package com.islandstudio.neon.api.dto.request.security.role

import java.io.Serializable

data class UpdateRoleRequestDTO(
    val roleId: Long,
    val roleCode: String?,
    val roleDisplayName: String?
): Serializable
