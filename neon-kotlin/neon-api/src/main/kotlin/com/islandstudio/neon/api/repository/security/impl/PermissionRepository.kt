package com.islandstudio.neon.api.repository.security.impl

import com.islandstudio.neon.api.IDatabaseContext
import com.islandstudio.neon.api.entity.security.PermissionEntity
import com.islandstudio.neon.api.repository.security.IPermissionRepository
import com.islandstudio.neon.api.schema.neon_data.tables.records.TPermissionRecord
import com.islandstudio.neon.api.schema.neon_data.tables.references.T_PERMISSION
import com.islandstudio.neon.shared.utils.data.ObjectMapper
import org.koin.core.annotation.Single

@Single
class PermissionRepository : IPermissionRepository, IDatabaseContext {
    override fun addPermission(permissionEntity: PermissionEntity): PermissionEntity? {
        runCatching {
            val record = ObjectMapper.mapTo(
                permissionEntity,
                TPermissionRecord::class.java
            )

            return dbContext()
                .insertInto(T_PERMISSION)
                .set(record)
                .returning()
                .fetchOneInto(PermissionEntity::class.java)
        }.getOrThrow()
    }

    override fun updatePermission(permissionEntity: PermissionEntity): PermissionEntity? {
        runCatching {
            val record = ObjectMapper.mapTo(
                permissionEntity,
                TPermissionRecord::class.java
            )

            return dbContext()
                .update(T_PERMISSION)
                .set(record)
                .where(T_PERMISSION.PERMISSION_ID.eq(record.permissionId))
                .returning()
                .fetchOneInto(PermissionEntity::class.java)
        }.getOrThrow()
    }

    override fun getAll(): List<PermissionEntity> {
        runCatching {
            return dbContext()
                .fetch(T_PERMISSION)
                .into(PermissionEntity::class.java)
        }.getOrThrow()
    }

    override fun getById(permissionId: Long): PermissionEntity? {
        runCatching {
            return dbContext()
                .select()
                .from(T_PERMISSION)
                .where(T_PERMISSION.PERMISSION_ID.eq(permissionId))
                .fetchOneInto(PermissionEntity::class.java)
        }.getOrThrow()
    }

    override fun getByPermissionCode(permissionCode: String): PermissionEntity? {
        runCatching {
            return dbContext()
                .select()
                .from(T_PERMISSION)
                .where(T_PERMISSION.PERMISSION_CODE.eq(permissionCode))
                .fetchOneInto(PermissionEntity::class.java)
        }.getOrThrow()
    }

    override fun getChildPermissions(parentPermissionId: Long): List<PermissionEntity> {
        runCatching {
            return dbContext()
                .select()
                .from(T_PERMISSION)
                .where(T_PERMISSION.PARENT_PERMISSION_ID.eq(parentPermissionId))
                .fetchInto(PermissionEntity::class.java)
        }.getOrThrow()
    }

    override fun deleteById(permissionId: Long): Boolean {
        runCatching {
            val deletedCount = dbContext()
                .deleteFrom(T_PERMISSION)
                .where(T_PERMISSION.PERMISSION_ID.eq(permissionId))
                .execute()

            return deletedCount == 1
        }.getOrThrow()
    }

    override fun deleteByPermissionCode(permissionCode: String): Boolean {
        runCatching {
            val deletedCount = dbContext()
                .deleteFrom(T_PERMISSION)
                .where(T_PERMISSION.PERMISSION_CODE.eq(permissionCode))
                .execute()

            return deletedCount == 1
        }.getOrThrow()
    }

    override fun existById(permissionId: Long): Boolean {
        runCatching {
            return dbContext()
                .fetchExists(
                    T_PERMISSION,
                    T_PERMISSION.PERMISSION_ID.eq(permissionId)
                )
        }.getOrThrow()
    }

    override fun existByPermissionCode(permissionCode: String): Boolean {
        runCatching {
            return dbContext()
                .fetchExists(
                    T_PERMISSION,
                    T_PERMISSION.PERMISSION_CODE.eq(permissionCode)
                )
        }.getOrThrow()
    }
}