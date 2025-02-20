package com.islandstudio.neon.api.dto.request.security.permission

import java.io.Serializable

data class BatchRemovePermissionRequestDTO(
    val permissionList: List<RemovePermissionRequestDTO>
): Serializable
