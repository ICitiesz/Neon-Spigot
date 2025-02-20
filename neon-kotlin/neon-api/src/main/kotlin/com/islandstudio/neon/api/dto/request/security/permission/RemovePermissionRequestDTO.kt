package com.islandstudio.neon.api.dto.request.security.permission

import java.io.Serializable

data class RemovePermissionRequestDTO(
    val permissionId: Long? = null,
    val permissionCode: String? = null
): Serializable
