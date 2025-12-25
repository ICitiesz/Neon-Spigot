package com.islandstudio.neon.apinew.repository.player

import com.islandstudio.neon.apinew.connection.UserContext
import com.islandstudio.neon.apinew.entity.player.Permission

interface IPermissionRepository {
    suspend fun addPermissionList(context: UserContext, permissionList: ArrayList<Permission>): ArrayList<Permission>
    suspend fun getAll(): ArrayList<Permission>
    suspend fun removeListById(ids: ArrayList<Long>): Boolean
    suspend fun removeListByCode(codes: ArrayList<String>): Boolean
}