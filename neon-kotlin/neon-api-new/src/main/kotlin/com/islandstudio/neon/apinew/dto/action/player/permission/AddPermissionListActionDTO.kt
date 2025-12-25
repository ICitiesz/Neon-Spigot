package com.islandstudio.neon.apinew.dto.action.player.permission

data class AddPermissionListActionDTO(
    val permissionList: ArrayList<PermissionActionDTO>
) {
    data class PermissionActionDTO(
        val name: String,
        val code: String,
        val description: String,
        val parentPermissionId: Long? = null,
        val subPermissions: List<PermissionActionDTO> = arrayListOf()
    )
}
