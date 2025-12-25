package com.islandstudio.neon.apinew.repository.player.impl

import com.islandstudio.neon.apinew.connection.NeonDatabaseContext
import com.islandstudio.neon.apinew.connection.UserContext
import com.islandstudio.neon.apinew.entity.player.Permission
import com.islandstudio.neon.apinew.repository.BaseRepository
import com.islandstudio.neon.apinew.repository.player.IPermissionRepository
import com.islandstudio.neon.apinew.schema.tables.PermissionTable
import com.islandstudio.neon.apinew.schema.tables.references.PERMISSION_TABLE
import org.koin.core.annotation.Single

@Single
class PermissionRepository(databaseContext: NeonDatabaseContext):
    BaseRepository<Permission, PermissionTable>(databaseContext, PERMISSION_TABLE),
    IPermissionRepository
{
    override suspend fun addPermissionList(context: UserContext, permissionList: ArrayList<Permission>): ArrayList<Permission> {
        return insertEntityListReturnAsync(Permission::class.java, permissionList.map { it.updateCreatedModified(context.contextName) }.toCollection(ArrayList()))
    }

    override suspend fun getAll(): ArrayList<Permission> {
        return getAllEntitiesAsync(Permission::class.java)
    }

    override suspend fun removeListById(ids: ArrayList<Long>): Boolean {
        return deleteEntityAsync {
            add(entityTable.ID.`in`(ids))
            this
        } > 0
    }

    override suspend fun removeListByCode(codes: ArrayList<String>): Boolean {
        return deleteEntityAsync {
            add(entityTable.CODE.`in`(codes))
            this
        } > 0
    }

}