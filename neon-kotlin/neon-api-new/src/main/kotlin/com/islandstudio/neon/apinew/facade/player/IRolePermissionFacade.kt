package com.islandstudio.neon.apinew.facade.player

import com.islandstudio.neon.apinew.dto.ResultProvider
import com.islandstudio.neon.apinew.dto.action.player.permission.AddRolePermisionActionDTO

interface IRolePermissionFacade {
    suspend fun addRolePermissionList(action: AddRolePermisionActionDTO): ResultProvider<Int>
    suspend fun removeRolePermissionListByIds(ids: ArrayList<Long>): ResultProvider<Boolean>
    suspend fun isRolePermissionExistByPermissionCodes(roleCode: String, permissionCodes: ArrayList<String>): ResultProvider<Boolean>
}