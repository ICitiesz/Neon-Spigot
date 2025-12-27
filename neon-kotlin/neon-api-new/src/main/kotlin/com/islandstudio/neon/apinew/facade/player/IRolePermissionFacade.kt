package com.islandstudio.neon.apinew.facade.player

import com.islandstudio.neon.apinew.dto.ResultProvider
import com.islandstudio.neon.apinew.dto.action.player.permission.AddRolePermisionActionDTO
import com.islandstudio.neon.apinew.dto.result.player.permission.RolePermissionDetailResultDTO

interface IRolePermissionFacade {
    suspend fun addRolePermissionList(action: AddRolePermisionActionDTO): ResultProvider<Int>
    suspend fun getRolePermissionDetailList(roleId: Long): ResultProvider<ArrayList<RolePermissionDetailResultDTO>>
    suspend fun removeRolePermissionListByIds(ids: ArrayList<Long>): ResultProvider<Boolean>
    suspend fun isRolePermissionExistByPermissionCodes(roleCode: String, permissionCodes: ArrayList<String>): ResultProvider<Boolean>
}