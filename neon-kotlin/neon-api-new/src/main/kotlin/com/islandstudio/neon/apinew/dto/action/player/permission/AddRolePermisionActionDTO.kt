package com.islandstudio.neon.apinew.dto.action.player.permission

data class AddRolePermisionActionDTO(
    val roleCode: String,
    val rolePermissionList: List<RolePermissionActionDTO>
) {
    data class RolePermissionActionDTO(
        val permissionCode: String,
        val parentRolePermissionId: Long? = null,
        val subRolePermissions: ArrayList<RolePermissionActionDTO> = arrayListOf()
    )
}