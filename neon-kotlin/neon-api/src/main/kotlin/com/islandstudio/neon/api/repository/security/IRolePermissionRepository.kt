package com.islandstudio.neon.api.repository.security

import com.islandstudio.neon.api.entity.security.RolePermissionEntity

interface IRolePermissionRepository {
    fun addRolePermission(rolePermissionEntity: RolePermissionEntity): RolePermissionEntity?

    fun updateRolePermission(rolePermissionEntity: RolePermissionEntity): RolePermissionEntity?

    fun getAll(): List<RolePermissionEntity>

    fun getById(rolePermissionId: Long): RolePermissionEntity?

    fun getByRoleId(roleId: Long): List<RolePermissionEntity>

    fun getByPermissionId(permissionId: Long): List<RolePermissionEntity>

    fun getChildRolePermissions(parentRolePermissionId: Long): List<RolePermissionEntity>

    fun deleteById(rolePermissionId: Long): Int

    fun deleteByRoleId(roleId: Long): Int

    fun deleteByPermissionId(permissionId: Long): Int

    fun existById(rolePermissionId: Long): Boolean

    fun existByRoleIdPermissionId(roleId: Long, permissionId: Long): Boolean
}