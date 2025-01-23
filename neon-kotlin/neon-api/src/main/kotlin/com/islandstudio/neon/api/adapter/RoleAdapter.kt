package com.islandstudio.neon.api.adapter

import com.islandstudio.neon.api.dto.request.security.CreateRoleRequestDTO
import com.islandstudio.neon.api.entity.security.RoleEntity
import com.islandstudio.neon.api.service.IRoleService
import com.islandstudio.neon.shared.core.di.IComponentInjector
import org.koin.core.annotation.Single
import org.koin.core.component.inject

@Single
class RoleAdapter: IComponentInjector {
    private val roleService by inject<IRoleService>()

    fun createRole(request: CreateRoleRequestDTO) {
        roleService.createRole(request)
    }

    fun getRoleByRoleId(roleId: Long): RoleEntity? {
        return roleService.getRoleById(roleId)
    }
}