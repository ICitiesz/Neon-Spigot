package com.islandstudio.neon.api.dto.request.security.permission

import java.io.Serializable

data class AddPermissionRequestDTO(
    val permissionCode: String,
    val permissionDescription: String,
    val parentPermissionId: Long? = null
): Serializable
