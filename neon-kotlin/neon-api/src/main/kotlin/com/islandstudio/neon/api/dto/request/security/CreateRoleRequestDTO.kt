package com.islandstudio.neon.api.dto.request.security

import java.io.Serializable

data class CreateRoleRequestDTO(
    val roleDisplayName: String,
    val roleCode: String
): Serializable