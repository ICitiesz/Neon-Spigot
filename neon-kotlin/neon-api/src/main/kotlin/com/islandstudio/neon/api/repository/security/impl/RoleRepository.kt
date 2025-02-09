package com.islandstudio.neon.api.repository.security.impl

import com.islandstudio.neon.api.IDatabaseContext
import com.islandstudio.neon.api.entity.security.RoleEntity
import com.islandstudio.neon.api.repository.security.IRoleRepository
import com.islandstudio.neon.api.schema.neon_data.tables.records.TRoleRecord
import com.islandstudio.neon.api.schema.neon_data.tables.references.T_PLAYER_PROFILE
import com.islandstudio.neon.api.schema.neon_data.tables.references.T_ROLE
import com.islandstudio.neon.shared.utils.data.ObjectMapper
import org.koin.core.annotation.Single
import java.util.*

@Single
class RoleRepository: IRoleRepository, IDatabaseContext {
    override fun addRole(roleEntity: RoleEntity): RoleEntity? {
        runCatching {
            val record = ObjectMapper.mapTo(
                roleEntity,
                TRoleRecord::class.java
            )

            return dbContext()
                .insertInto(T_ROLE)
                .set(record)
                .returning()
                .fetchOneInto(RoleEntity::class.java)
        }.getOrThrow()
    }

    override fun updateRole(roleEntity: RoleEntity): RoleEntity? {
        runCatching {
            val record = ObjectMapper.mapTo(
                roleEntity,
                TRoleRecord::class.java
            )

            return dbContext()
                .update(T_ROLE)
                .set(record)
                .where(T_ROLE.ROLE_ID.eq(roleEntity.roleId))
                .returning()
                .fetchOneInto(RoleEntity::class.java)
        }.getOrThrow()
    }

    override fun getAll(): List<RoleEntity> {
        runCatching {
            return dbContext()
                .fetch(T_ROLE)
                .into(RoleEntity::class.java)
        }.getOrThrow()
    }

    override fun getById(roleId: Long): RoleEntity? {
        runCatching {
            return dbContext()
                .select()
                .from(T_ROLE)
                .where(T_ROLE.ROLE_ID.eq(roleId))
                .fetchOneInto(RoleEntity::class.java)
        }.getOrThrow()
    }

    override fun getByRoleCode(roleCode: String): RoleEntity? {
        runCatching {
            return dbContext()
                .select()
                .from(T_ROLE)
                .where(T_ROLE.ROLE_CODE.eq(roleCode))
                .fetchOneInto(RoleEntity::class.java)
        }.getOrThrow()
    }

    override fun deleteByRoleCode(roleCode: String): Int {
        runCatching {
            return dbContext()
                .deleteFrom(T_ROLE)
                .where(T_ROLE.ROLE_CODE.eq(roleCode))
                .execute()
        }.getOrThrow()
    }

    override fun existById(roleId: Long): Boolean {
        runCatching {
            return dbContext()
                .fetchExists(
                    T_ROLE,
                    T_ROLE.ROLE_ID.eq(roleId)
                )
        }.getOrThrow()
    }

    override fun existByRoleCode(roleCode: String): Boolean {
        runCatching {
            return dbContext()
                .fetchExists(
                    T_ROLE,
                    T_ROLE.ROLE_CODE.eq(roleCode)
                )
        }.getOrThrow()
    }

    override fun getPlayerRoleByUUID(uuid: UUID): RoleEntity? {
        runCatching {
            return dbContext()
                .select(T_ROLE.ROLE_ID, T_ROLE.ROLE_DISPLAY_NAME, T_ROLE.ROLE_CODE)
                .from(T_ROLE)
                .innerJoin(T_PLAYER_PROFILE).on(T_PLAYER_PROFILE.ROLE_ID.eq(T_ROLE.ROLE_ID))
                .where(T_PLAYER_PROFILE.PLAYER_UUID.eq(uuid))
                .fetchOneInto(RoleEntity::class.java)
        }.getOrThrow()
    }

}