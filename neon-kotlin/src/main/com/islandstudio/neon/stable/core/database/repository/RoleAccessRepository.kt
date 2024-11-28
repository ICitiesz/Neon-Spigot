package com.islandstudio.neon.stable.core.database.repository

import com.islandstudio.neon.stable.core.application.di.ModuleInjector
import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.pojos.RoleAccess
import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.records.DtRoleAccessRecord
import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.references.DT_ACCESS_PERMISSION
import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.references.DT_ROLE
import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.references.DT_ROLE_ACCESS
import com.islandstudio.neon.stable.player.nAccessPermission.Permission
import org.jooq.DSLContext
import org.koin.core.annotation.Single
import org.koin.core.component.inject

@Single
class RoleAccessRepository: ModuleInjector {
    private val dbContext by inject<DSLContext>()

    fun addRoleAccess(roleAccess: RoleAccess) {
        with(DtRoleAccessRecord(roleAccess)) {
            dbContext
                .insertInto(DT_ROLE_ACCESS)
                .set(this)
                .execute()
        }
    }

    fun updateRoleAccess(roleAccess: RoleAccess) {
        with(DtRoleAccessRecord(roleAccess)) {
            dbContext
                .update(DT_ROLE_ACCESS)
                .set(this)
                .where(DT_ROLE_ACCESS.ROLE_ACCESS_ID.eq(this.roleAccessId))
                .execute()
        }
    }

    fun removeByRoleAccessId(roleAccessId: Long) {
        dbContext.
        deleteFrom(DT_ROLE_ACCESS)
            .where(DT_ROLE_ACCESS.ROLE_ACCESS_ID.eq(roleAccessId))
            .execute()
    }

    fun isExistByRoleAccessId(roleAccesId: Long): Boolean {
        val sqlQuery =  dbContext
            .select()
            .from(DT_ROLE_ACCESS)
            .where(DT_ROLE_ACCESS.ROLE_ACCESS_ID.eq(roleAccesId))

        return dbContext.fetchExists(sqlQuery)
    }

    fun getAll(): List<RoleAccess> {
        return dbContext.fetch(DT_ROLE_ACCESS).into(RoleAccess::class.java)
    }

    fun getAllByRoleId(roleId: Long): List<RoleAccess> {
        return dbContext
            .selectFrom(DT_ROLE_ACCESS)
            .where(DT_ROLE_ACCESS.ROLE_ID.eq(roleId))
            .fetchInto(RoleAccess::class.java)
    }

    fun getByRoleAccessId(roleAccessId: Long): RoleAccess? {
        return dbContext
            .select()
            .from(DT_ROLE_ACCESS)
            .where(DT_ROLE_ACCESS.ROLE_ACCESS_ID.eq(roleAccessId))
            .fetchOneInto(RoleAccess::class.java)
    }

    fun getByPermission(roleId: Long, permissionId: Long, accessType: Permission.AccessType?): RoleAccess? {
        val sqlQuery = dbContext
            .select()
            .from(DT_ROLE_ACCESS)
            .innerJoin(DT_ROLE).on(DT_ROLE_ACCESS.ROLE_ID.eq(DT_ROLE.ROLE_ID))
            .innerJoin(DT_ACCESS_PERMISSION).on(DT_ROLE_ACCESS.PERMISSION_ID.eq(DT_ACCESS_PERMISSION.PERMISSION_ID))

        accessType?.let {
            sqlQuery
                .where(DT_ROLE_ACCESS.ROLE_ID.eq(roleId)
                    .and(DT_ROLE_ACCESS.PERMISSION_ID.eq(permissionId)
                        .and(DT_ROLE_ACCESS.ACCESS_TYPE.eq(accessType.toString()))
                    )
                )
        } ?: sqlQuery
            .where(DT_ROLE_ACCESS.ROLE_ID.eq(roleId)
                .and(DT_ROLE_ACCESS.PERMISSION_ID.eq(permissionId))
            )

        return sqlQuery.fetchOneInto(RoleAccess::class.java)
    }
}