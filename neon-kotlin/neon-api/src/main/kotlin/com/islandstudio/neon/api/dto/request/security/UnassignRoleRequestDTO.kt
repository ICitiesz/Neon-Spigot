package com.islandstudio.neon.api.dto.request.security

import java.io.Serializable
import java.util.*

data class UnassignRoleRequestDTO(
    val playerUUID: UUID
): Serializable
