package com.islandstudio.neon.apinew.facade.player

import com.islandstudio.neon.apinew.dto.ResultProvider
import com.islandstudio.neon.apinew.dto.action.player.permission.AddPermissionListActionDTO
import com.islandstudio.neon.apinew.entity.player.Permission

interface IPermissionFacade {
    suspend fun addPermissionList(action: AddPermissionListActionDTO): ResultProvider<Int>
    suspend fun removePermissionListByIds(ids: ArrayList<Long>): ResultProvider<Boolean>
    suspend fun getAllPermissions(): ResultProvider<ArrayList<Permission>>
}