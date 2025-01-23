package com.islandstudio.neon.api.repository.security.impl

import com.islandstudio.neon.api.IDatabaseContext
import com.islandstudio.neon.api.entity.security.RoleEntity
import com.islandstudio.neon.api.repository.security.IRoleRepository
import com.islandstudio.neon.api.schema.neon_data.tables.records.TRoleRecord
import com.islandstudio.neon.api.schema.neon_data.tables.references.T_PLAYER_PROFILE
import com.islandstudio.neon.api.schema.neon_data.tables.references.T_ROLE
import com.islandstudio.neon.shared.utils.data.IObjectMapper
import org.koin.core.annotation.Single
import java.util.*

@Single
class RoleRepository: IRoleRepository, IDatabaseContext, IObjectMapper {
    override fun addRole(roleEntity: RoleEntity) {
        val record = mapTo(roleEntity, TRoleRecord::class.java)

        dbContext()
            .insertInto(T_ROLE)
            .set(record)
            .execute()
    }

    override fun updateRole(roleEntity: RoleEntity) {
        val record = mapTo(roleEntity, TRoleRecord::class.java)

        dbContext()
            .update(T_ROLE)
            .set(record)
            .where(T_ROLE.ROLE_ID.eq(roleEntity.roleId))
    }

    override fun getAll(): List<RoleEntity> {
        return dbContext()
            .fetch(T_ROLE)
            .into(RoleEntity::class.java)
    }

    override fun getById(roleId: Long): RoleEntity? {
        return dbContext()
            .select()
            .from(T_ROLE)
            .where(T_ROLE.ROLE_ID.eq(roleId))
            .fetchOneInto(RoleEntity::class.java)
    }

    override fun getByRoleCode(roleCode: String): RoleEntity? {
        return dbContext()
            .select()
            .from(T_ROLE)
            .where(T_ROLE.ROLE_CODE.eq(roleCode))
            .fetchOneInto(RoleEntity::class.java)
    }

    override fun deleteByRoleCode(roleCode: String) {
        dbContext()
            .deleteFrom(T_ROLE)
            .where(T_ROLE.ROLE_CODE.eq(roleCode))
            .execute()
    }

    override fun existById(roleId: Long): Boolean {
        val sqlQuery = dbContext()
            .select()
            .from(T_ROLE)
            .where(T_ROLE.ROLE_ID.eq(roleId))

        return dbContext().fetchExists(sqlQuery)
    }

    override fun existByRoleCode(roleCode: String): Boolean {
        val sqlQuery = dbContext()
            .select()
            .from(T_ROLE)
            .where(T_ROLE.ROLE_CODE.eq(roleCode))

        return dbContext().fetchExists(sqlQuery)
    }

    override fun getPlayerRoleByUUID(uuid: UUID): RoleEntity? {
        return dbContext()
            .select(T_ROLE.ROLE_ID, T_ROLE.ROLE_DISPLAY_NAME, T_ROLE.ROLE_CODE)
            .from(T_ROLE)
            .innerJoin(T_PLAYER_PROFILE).on(T_PLAYER_PROFILE.ROLE_ID.eq(T_ROLE.ROLE_ID))
            .where(T_PLAYER_PROFILE.PLAYER_UUID.eq(uuid))
            .fetchOneInto(RoleEntity::class.java)
    }

}