package com.islandstudio.neon.api.repository.security

import com.islandstudio.neon.api.dto.security.RolePermissionDto

interface RolePermissionRepository {
    /* Old reference */
//    suspend fun addList(context: ApiContext, rolePermissionList: ArrayList<RolePermission>): ArrayList<RolePermission>
//    suspend fun getListDetailByPermissionCodes(roleCode: String, permissionCodes: ArrayList<String>): ArrayList<RolePermissionDetailResultDTO>
//    suspend fun getListDetailById(roleId: Long): ArrayList<RolePermissionDetailResultDTO>

    suspend fun addRolePermissionList(): List<RolePermissionDto>
    suspend fun findByRoleId(roleId: Long): List<RolePermissionDto>
    suspend fun removeListById(ids: List<Long>): Boolean
}