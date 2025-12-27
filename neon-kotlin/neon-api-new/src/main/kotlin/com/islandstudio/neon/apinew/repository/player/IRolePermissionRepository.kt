package com.islandstudio.neon.apinew.repository.player

import com.islandstudio.neon.apinew.connection.UserContext
import com.islandstudio.neon.apinew.dto.result.player.permission.RolePermissionDetailResultDTO
import com.islandstudio.neon.apinew.entity.player.RolePermission

interface IRolePermissionRepository {
    suspend fun addList(context: UserContext, rolePermissionList: ArrayList<RolePermission>): ArrayList<RolePermission>
    suspend fun getListByRoleId(roleId: Long): ArrayList<RolePermission>
    suspend fun removeListByIds(ids: ArrayList<Long>): Boolean
    suspend fun getListDetailByPermissionCodes(roleCode: String, permissionCodes: ArrayList<String>): ArrayList<RolePermissionDetailResultDTO>
    suspend fun getListDetailById(roleId: Long): ArrayList<RolePermissionDetailResultDTO>
}