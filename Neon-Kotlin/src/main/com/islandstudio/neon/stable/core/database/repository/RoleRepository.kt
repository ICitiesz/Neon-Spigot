package com.islandstudio.neon.stable.core.database.repository

import com.islandstudio.neon.stable.core.application.di.ModuleInjector
import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.pojos.Role
import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.records.DtRoleRecord
import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.references.DT_PLAYER_PROFILE
import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.references.DT_ROLE
import org.jooq.DSLContext
import org.koin.core.annotation.Singleton
import org.koin.core.component.inject
import java.util.*

@Singleton
class RoleRepository: ModuleInjector {
    private val dbContext by inject<DSLContext>()

    fun addRole(role: Role) {
        with(DtRoleRecord(role)) {
            dbContext.insertInto(DT_ROLE)
                .set(this)
                .execute()
        }
    }

    fun updateRole(role: Role) {
        with(DtRoleRecord(role)) {
            dbContext
                .update(DT_ROLE)
                .set(this)
                .where(DT_ROLE.ROLE_ID.eq(role.roleId))
        }
    }

    fun removeByRoleCode(roleCode: String) {
        dbContext
            .deleteFrom(DT_ROLE)
            .where(DT_ROLE.ROLE_CODE.eq(roleCode))
            .execute()
    }

    fun getByRoleId(roleId: Long): Role? {
        return dbContext
            .select()
            .from(DT_ROLE)
            .where(DT_ROLE.ROLE_ID.eq(roleId))
            .fetchOneInto(Role::class.java)
    }

    fun getByRoleCode(roleCode: String): Role? {
        return dbContext
            .select()
            .from(DT_ROLE)
            .where(DT_ROLE.ROLE_CODE.eq(roleCode))
            .fetchOneInto(Role::class.java)
    }

    fun isExistByRoleId(roleId: Long): Boolean {
        val sqlQuery = dbContext
            .select()
            .from(DT_ROLE)
            .where(DT_ROLE.ROLE_ID.eq(roleId))

        return dbContext.fetchExists(sqlQuery)
    }

    fun isExistByRoleCode(roleCode: String): Boolean {
        val sqlQuery = dbContext
            .select()
            .from(DT_ROLE)
            .where(DT_ROLE.ROLE_CODE.eq(roleCode))

        return dbContext.fetchExists(sqlQuery)
    }

    fun getAll(): List<Role> {
        return dbContext.fetch(DT_ROLE).into(Role::class.java)
    }

    fun getPlayerRoleByPlayerUUID(playerUUID: UUID): Role? {
        return dbContext
            .select(DT_ROLE.ROLE_ID, DT_ROLE.ROLE_DISPLAY_NAME, DT_ROLE.ROLE_CODE)
            .from(DT_ROLE)
            .innerJoin(DT_PLAYER_PROFILE).on(DT_PLAYER_PROFILE.ROLE_ID.eq(DT_ROLE.ROLE_ID))
            .where(DT_PLAYER_PROFILE.PLAYER_UUID.eq(playerUUID))
            .fetchOneInto(Role::class.java)
    }
}