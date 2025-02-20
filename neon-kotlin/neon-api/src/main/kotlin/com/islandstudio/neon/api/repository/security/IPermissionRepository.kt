package com.islandstudio.neon.api.repository.security

import com.islandstudio.neon.api.entity.security.PermissionEntity

interface IPermissionRepository {
    fun addPermission(permissionEntity: PermissionEntity): PermissionEntity?

    fun batchAddPermission(permissionEntityList: List<PermissionEntity>): List<PermissionEntity>

    fun updatePermission(permissionEntity: PermissionEntity): PermissionEntity?

    fun getAll(): List<PermissionEntity>

    fun getById(permissionId: Long): PermissionEntity?

    fun getByPermissionCode(permissionCode: String): PermissionEntity?

    fun getChildPermissions(parentPermissionId: Long): List<PermissionEntity>

    fun deleteById(permissionId: Long): Int

    fun batchDeleteById(idList: List<Long>): Int

    fun deleteByPermissionCode(permissionCode: String): Int

    fun batchDeleteByPermissionCode(permissionCodeList: List<String>): Int

    fun existById(permissionId: Long): Boolean

    fun existByPermissionCode(permissionCode: String): Boolean
}