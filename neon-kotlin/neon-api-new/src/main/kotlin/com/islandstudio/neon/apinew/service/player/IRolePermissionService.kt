package com.islandstudio.neon.apinew.service.player

import com.islandstudio.neon.apinew.connection.UserContext
import com.islandstudio.neon.apinew.dto.ResultProvider
import com.islandstudio.neon.apinew.dto.action.player.permission.AddRolePermisionActionDTO

interface IRolePermissionService {
    suspend fun addRolePermissionList(context: UserContext, action: AddRolePermisionActionDTO): ResultProvider<Int>
    suspend fun removeRolePermissionListByIds(ids: ArrayList<Long>): ResultProvider<Boolean>
    suspend fun isRolePermissionExistByPermissionCodes(roleCode: String, permissionCodes: ArrayList<String>): ResultProvider<Boolean>
}