package com.islandstudio.neon.api.dto.request.security.permission

import java.io.Serializable

data class BatchAddPermissionRequestDTO(
    val permissions: List<PermissionDTO>
): Serializable
