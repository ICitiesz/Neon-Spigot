package com.islandstudio.neon.api.repository.security

import com.islandstudio.neon.api.entity.security.RoleEntity
import java.util.*

interface IRoleRepository {
    fun addRole(roleEntity: RoleEntity)

    fun updateRole(roleEntity: RoleEntity)

    fun getAll(): List<RoleEntity>

    fun getById(roleId: Long): RoleEntity?

    fun getByRoleCode(roleCode: String): RoleEntity?

    fun deleteByRoleCode(roleCode: String)

    fun existById(roleId: Long): Boolean

    fun existByRoleCode(roleCode: String): Boolean

    fun getPlayerRoleByUUID(uuid: UUID): RoleEntity?
}