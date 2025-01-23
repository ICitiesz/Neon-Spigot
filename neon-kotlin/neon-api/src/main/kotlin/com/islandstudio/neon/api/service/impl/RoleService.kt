package com.islandstudio.neon.api.service.impl

import com.islandstudio.neon.api.dto.request.security.CreateRoleRequestDTO
import com.islandstudio.neon.api.entity.security.RoleEntity
import com.islandstudio.neon.api.repository.security.IRoleRepository
import com.islandstudio.neon.api.service.IRoleService
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.utils.data.IObjectMapper
import org.koin.core.annotation.Single
import org.koin.core.component.inject

@Single
class RoleService: IRoleService, IComponentInjector, IObjectMapper {
    private val roleRepository by inject<IRoleRepository>()

    override fun createRole(request: CreateRoleRequestDTO) {
        roleRepository.addRole(request.roleEntity)
    }

    override fun getRoleById(roleId: Long): RoleEntity?  {
        return roleRepository.getById(roleId)
    }

    override fun getRoleByRoleCode(roleCode: String): RoleEntity? {
        return roleRepository.getByRoleCode(roleCode)
    }
}