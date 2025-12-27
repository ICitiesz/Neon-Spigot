package com.islandstudio.neon.apinew.repository.player.impl

import com.islandstudio.neon.apinew.connection.NeonDatabaseContext
import com.islandstudio.neon.apinew.connection.UserContext
import com.islandstudio.neon.apinew.dto.result.player.permission.RolePermissionDetailResultDTO
import com.islandstudio.neon.apinew.entity.player.RolePermission
import com.islandstudio.neon.apinew.repository.BaseRepository
import com.islandstudio.neon.apinew.repository.player.IRolePermissionRepository
import com.islandstudio.neon.apinew.schema.tables.RolePermissionTable
import com.islandstudio.neon.apinew.schema.tables.references.PERMISSION_TABLE
import com.islandstudio.neon.apinew.schema.tables.references.PLAYER_ROLE_TABLE
import com.islandstudio.neon.apinew.schema.tables.references.ROLE_PERMISSION_TABLE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single

@Single
class RolePermissionRepository(databaseContext: NeonDatabaseContext):
    BaseRepository<RolePermission, RolePermissionTable>(databaseContext, ROLE_PERMISSION_TABLE),
    IRolePermissionRepository
{
    override suspend fun addList(context: UserContext, rolePermissionList: ArrayList<RolePermission>): ArrayList<RolePermission> {
        return insertEntityListReturnAsync(
            RolePermission::class.java,
            rolePermissionList.map { it.updateCreatedModified(context.contextName) }.toCollection(ArrayList())
        )
    }

    override suspend fun getListByRoleId(roleId: Long): ArrayList<RolePermission> {
        return getListEntityAsync(RolePermission::class.java) {
            add(entityTable.ROLEID.eq(roleId))
            this
        }
    }

    override suspend fun removeListByIds(ids: ArrayList<Long>): Boolean {
        return deleteEntityAsync {
            add(entityTable.ID.`in`(ids))
            this
        } > 0
    }

    override suspend fun getListDetailByPermissionCodes(roleCode: String, permissionCodes: ArrayList<String>): ArrayList<RolePermissionDetailResultDTO> {
        return withContext(Dispatchers.IO) {
            databaseContext
                .select(
                    ROLE_PERMISSION_TABLE.ID,
                    ROLE_PERMISSION_TABLE.PARENTID,
                    ROLE_PERMISSION_TABLE.ROLEID,
                    PLAYER_ROLE_TABLE.CODE,
                    ROLE_PERMISSION_TABLE.PERMISSIONID,
                    PERMISSION_TABLE.CODE
                )
                .from(ROLE_PERMISSION_TABLE)
                .join(PLAYER_ROLE_TABLE)
                .on(ROLE_PERMISSION_TABLE.ROLEID.eq(PLAYER_ROLE_TABLE.ID))
                .join(PERMISSION_TABLE)
                .on(ROLE_PERMISSION_TABLE.PERMISSIONID.eq(PERMISSION_TABLE.ID))
                .where(PLAYER_ROLE_TABLE.CODE.eq(roleCode).and(PERMISSION_TABLE.CODE.`in`(permissionCodes)))
                .fetch()
                .map {
                    RolePermissionDetailResultDTO(
                        it[ROLE_PERMISSION_TABLE.ID]!!,
                        it[ROLE_PERMISSION_TABLE.PARENTID],
                        it[ROLE_PERMISSION_TABLE.ROLEID]!!,
                        it[PLAYER_ROLE_TABLE.CODE]!!,
                        it[ROLE_PERMISSION_TABLE.PERMISSIONID]!!,
                        it[PERMISSION_TABLE.CODE]!!
                    )
                }
                .toCollection(ArrayList())
        }
    }

    override suspend fun getListDetailById(roleId: Long): ArrayList<RolePermissionDetailResultDTO> {
        return withContext(Dispatchers.IO) {
            databaseContext
                .select(
                    ROLE_PERMISSION_TABLE.ID,
                    ROLE_PERMISSION_TABLE.PARENTID,
                    ROLE_PERMISSION_TABLE.ROLEID,
                    PLAYER_ROLE_TABLE.CODE,
                    ROLE_PERMISSION_TABLE.PERMISSIONID,
                    PERMISSION_TABLE.CODE
                )
                .from(ROLE_PERMISSION_TABLE)
                .join(PLAYER_ROLE_TABLE)
                .on(ROLE_PERMISSION_TABLE.ROLEID.eq(PLAYER_ROLE_TABLE.ID))
                .join(PERMISSION_TABLE)
                .on(ROLE_PERMISSION_TABLE.PERMISSIONID.eq(PERMISSION_TABLE.ID))
                .where(PLAYER_ROLE_TABLE.ID.eq(roleId))
                .fetch()
                .map {
                    RolePermissionDetailResultDTO(
                        it[ROLE_PERMISSION_TABLE.ID]!!,
                        it[ROLE_PERMISSION_TABLE.PARENTID],
                        it[ROLE_PERMISSION_TABLE.ROLEID]!!,
                        it[PLAYER_ROLE_TABLE.CODE]!!,
                        it[ROLE_PERMISSION_TABLE.PERMISSIONID]!!,
                        it[PERMISSION_TABLE.CODE]!!
                    )
                }
                .toCollection(ArrayList())
        }
    }

}