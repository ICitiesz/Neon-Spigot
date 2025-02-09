package com.islandstudio.neon.api.repository.security.impl

import com.islandstudio.neon.api.IDatabaseContext
import com.islandstudio.neon.api.entity.security.RolePermissionEntity
import com.islandstudio.neon.api.repository.security.IRolePermissionRepository
import com.islandstudio.neon.api.schema.neon_data.tables.records.TRolePermissionRecord
import com.islandstudio.neon.api.schema.neon_data.tables.references.T_ROLE_PERMISSION
import com.islandstudio.neon.shared.utils.data.ObjectMapper
import org.koin.core.annotation.Single

@Single
class RolePermissionRepository: IRolePermissionRepository, IDatabaseContext {
    override fun addRolePermission(rolePermissionEntity: RolePermissionEntity): RolePermissionEntity? {
        runCatching {
            val record = ObjectMapper.mapTo(
                rolePermissionEntity,
                TRolePermissionRecord::class.java
            )

            return dbContext()
                .insertInto(T_ROLE_PERMISSION)
                .set(record)
                .returning()
                .fetchOneInto(RolePermissionEntity::class.java)
        }.getOrThrow()
    }

    override fun updateRolePermission(rolePermissionEntity: RolePermissionEntity): RolePermissionEntity? {
        runCatching {
            val record = ObjectMapper.mapTo(
                rolePermissionEntity,
                TRolePermissionRecord::class.java
            )

            return dbContext()
                .update(T_ROLE_PERMISSION)
                .set(record)
                .where(T_ROLE_PERMISSION.ROLE_PERMISSION_ID.eq(record.rolePermissionId))
                .returning()
                .fetchOneInto(RolePermissionEntity::class.java)
        }.getOrThrow()
    }

    override fun getAll(): List<RolePermissionEntity> {
        runCatching {
            return dbContext()
                .fetch(T_ROLE_PERMISSION)
                .into(RolePermissionEntity::class.java)
        }.getOrThrow()
    }

    override fun getById(rolePermissionId: Long): RolePermissionEntity? {
        runCatching {
            return dbContext()
                .select()
                .from(T_ROLE_PERMISSION)
                .where(T_ROLE_PERMISSION.ROLE_PERMISSION_ID.eq(rolePermissionId))
                .fetchOneInto(RolePermissionEntity::class.java)
        }.getOrThrow()
    }

    override fun getByRoleId(roleId: Long): List<RolePermissionEntity> {
        runCatching {
            return dbContext()
                .select()
                .from(T_ROLE_PERMISSION)
                .where(T_ROLE_PERMISSION.ROLE_ID.eq(roleId))
                .fetchInto(RolePermissionEntity::class.java)
        }.getOrThrow()
    }

    override fun getByPermissionId(permissionId: Long): List<RolePermissionEntity> {
        runCatching {
            return dbContext()
                .select()
                .from(T_ROLE_PERMISSION)
                .where(T_ROLE_PERMISSION.PERMISSION_ID.eq(permissionId))
                .fetchInto(RolePermissionEntity::class.java)
        }.getOrThrow()
    }

    override fun getChildRolePermissions(parentRolePermissionId: Long): List<RolePermissionEntity> {
        runCatching {
            return dbContext()
                .select()
                .from(T_ROLE_PERMISSION)
                .where(T_ROLE_PERMISSION.PARENT_ROLE_PERMISSION_ID.eq(parentRolePermissionId))
                .fetchInto(RolePermissionEntity::class.java)
        }.getOrThrow()
    }

    override fun deleteById(rolePermissionId: Long): Int {
        runCatching {
            return dbContext()
                .deleteFrom(T_ROLE_PERMISSION)
                .where(T_ROLE_PERMISSION.ROLE_PERMISSION_ID.eq(rolePermissionId))
                .execute()
        }.getOrThrow()
    }

    override fun deleteByRoleId(roleId: Long): Int {
        runCatching {
            return dbContext()
                .deleteFrom(T_ROLE_PERMISSION)
                .where(T_ROLE_PERMISSION.ROLE_ID.eq(roleId))
                .execute()
        }.getOrThrow()
    }

    override fun deleteByPermissionId(permissionId: Long): Int {
        runCatching {
            return dbContext()
                .deleteFrom(T_ROLE_PERMISSION)
                .where(T_ROLE_PERMISSION.PERMISSION_ID.eq(permissionId))
                .execute()
        }.getOrThrow()
    }

    override fun existById(rolePermissionId: Long): Boolean {
        runCatching {
            return dbContext()
                .fetchExists(
                    T_ROLE_PERMISSION,
                T_ROLE_PERMISSION.ROLE_PERMISSION_ID.eq(rolePermissionId)
                )
        }.getOrThrow()
    }

    override fun existByRoleIdPermissionId(roleId: Long, permissionId: Long): Boolean {
        runCatching {
            return dbContext()
                .fetchExists(
                    T_ROLE_PERMISSION,
                    T_ROLE_PERMISSION.ROLE_ID.eq(roleId).and(T_ROLE_PERMISSION.PERMISSION_ID.eq(permissionId))
                )
        }.getOrThrow()
    }
}