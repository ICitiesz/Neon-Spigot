package com.islandstudio.neon.api.dto.request.security.permission

import java.io.Serializable

data class PermissionDTO(
    val permissionCode: String,
    val permissionDescription: String,
    val subPermissions: List<PermissionDTO> = arrayListOf(),
    val parentPermissionId: Long? = null
): Serializable
