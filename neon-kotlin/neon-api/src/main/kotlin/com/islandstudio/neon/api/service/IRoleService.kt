package com.islandstudio.neon.api.service

import com.islandstudio.neon.api.dto.request.security.CreateRoleRequestDTO
import com.islandstudio.neon.api.entity.security.RoleEntity

interface IRoleService {
    fun createRole(request: CreateRoleRequestDTO)

    fun getRoleById(roleId: Long): RoleEntity?

    fun getRoleByRoleCode(roleCode: String): RoleEntity?
}