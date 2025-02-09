package com.islandstudio.neon.api.adapter.security

import com.islandstudio.neon.api.dto.action.ActionResult
import com.islandstudio.neon.api.dto.action.ActionStatus
import com.islandstudio.neon.api.dto.action.IActionResult
import com.islandstudio.neon.api.dto.request.security.CreateRoleRequestDTO
import com.islandstudio.neon.api.dto.request.security.role.GetRoleRequestDTO
import com.islandstudio.neon.api.dto.request.security.role.RemoveRoleRequestDTO
import com.islandstudio.neon.api.entity.security.RoleEntity
import com.islandstudio.neon.api.service.security.IRoleService
import com.islandstudio.neon.shared.core.di.IComponentInjector
import org.koin.core.annotation.Single
import org.koin.core.component.inject

@Single
class RoleAdapter: IComponentInjector {
    private val roleService by inject<IRoleService>()

    fun createRole(invoker: String?, request: CreateRoleRequestDTO): IActionResult<RoleEntity?> {
        return roleService.createRole(invoker, request)
    }

    fun getRole(request: GetRoleRequestDTO): IActionResult<RoleEntity?> {
        return when {
            request.roleId != null -> roleService.getRoleById(request)
            !request.roleCode.isNullOrEmpty() -> roleService.getRoleByRoleCode(request)

            else -> ActionResult<RoleEntity?>()
                .withStatus(ActionStatus.INVALID_REQUEST_FIELD)
        }
    }

    fun removeRole(request: RemoveRoleRequestDTO): IActionResult<Int> {
        return roleService.removeRoleByRoleCode(request)
    }
}