package com.islandstudio.neon.api.repository.security

import com.islandstudio.neon.api.entity.security.PermissionEntity

interface IPermissionRepository {
    fun addPermission(permissionEntity: PermissionEntity): PermissionEntity?

    fun updatePermission(permissionEntity: PermissionEntity): PermissionEntity?

    fun getAll(): List<PermissionEntity>

    fun getById(permissionId: Long): PermissionEntity?

    fun getByPermissionCode(permissionCode: String): PermissionEntity?

    fun getChildPermissions(parentPermissionId: Long): List<PermissionEntity>

    fun deleteById(permissionId: Long): Boolean

    fun deleteByPermissionCode(permissionCode: String): Boolean

    fun existById(permissionId: Long): Boolean

    fun existByPermissionCode(permissionCode: String): Boolean
}