package com.islandstudio.neon.api.dto.request.security

import java.io.Serializable
import java.util.*

data class AssignRoleRequestDTO(
    val playerUUID: UUID,
    val roleCode: String
): Serializable
