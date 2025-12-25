package com.islandstudio.neon.apinew.service.player

import com.islandstudio.neon.apinew.connection.UserContext
import com.islandstudio.neon.apinew.dto.ResultProvider
import com.islandstudio.neon.apinew.dto.action.player.permission.AddPermissionListActionDTO
import com.islandstudio.neon.apinew.entity.player.Permission

interface IPermissionService {
    suspend fun addPermissionList(context: UserContext, action: AddPermissionListActionDTO): ResultProvider<Int>
    suspend fun removePermissionListByIds(ids: ArrayList<Long>): ResultProvider<Boolean>
    suspend fun getAllPermissions(): ResultProvider<ArrayList<Permission>>
}