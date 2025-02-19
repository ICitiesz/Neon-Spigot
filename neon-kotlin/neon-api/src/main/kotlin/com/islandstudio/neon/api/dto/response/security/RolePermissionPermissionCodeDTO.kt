package com.islandstudio.neon.api.dto.response.security

import java.io.Serializable

data class RolePermissionPermissionCodeDTO(
    var rolePermissionId: Long? = null,
    var roleId: Long? = null,
    var permissionId: Long? = null,
    var permissionCode: String? = null,
    var parentRolePermissionId: Long? = null
): Serializable
