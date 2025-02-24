package com.islandstudio.neon.api.dto.request.security.permission

import java.io.Serializable

data class GrantRolePermissionRequestDTO(
    val roleId: Long? = null,
    val permissionId: Long,
    val subRolePermissions: ArrayList<GrantRolePermissionRequestDTO> = arrayListOf(),
    val parentRolePermissionId: Long? = null
): Serializable