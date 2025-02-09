package com.islandstudio.neon.api.adapter

import com.islandstudio.neon.api.dto.action.ActionResult
import com.islandstudio.neon.api.dto.action.ActionStatus
import com.islandstudio.neon.api.dto.action.IActionResult
import com.islandstudio.neon.api.dto.request.security.permission.GetRolePermissionRequestDTO
import com.islandstudio.neon.api.dto.request.security.permission.GrantRolePermissionRequestDTO
import com.islandstudio.neon.api.dto.request.security.permission.RevokeRolePermissionRequestDTO
import com.islandstudio.neon.api.dto.response.security.RolePermissionListResponseDTO
import com.islandstudio.neon.api.entity.security.RolePermissionEntity
import com.islandstudio.neon.api.service.security.IRolePermissionService
import com.islandstudio.neon.shared.core.di.IComponentInjector
import org.koin.core.annotation.Single
import org.koin.core.component.inject

@Single
class RolePermissionAdapter: IComponentInjector {
    private val rolePermissionService by inject<IRolePermissionService>()

    fun grantRolePermission(invoker: String?, request: GrantRolePermissionRequestDTO): IActionResult<RolePermissionEntity?> {
        return rolePermissionService.addRolePermission(invoker, request)
    }

    fun revokeRolePermission(request: RevokeRolePermissionRequestDTO): IActionResult<Int> {
        return when {
            request.rolePermissionId != null -> rolePermissionService.removeRolePermissionById(request)
            request.roleId != null -> rolePermissionService.removeRolePermissionByRoleId(request)

            else -> ActionResult<Int>()
                .withStatus(ActionStatus.NULL_OR_EMPTY_FIELD)
                .withLogMessage("Invalid request field!")
        }
    }

    fun getRolePermission(request: GetRolePermissionRequestDTO): IActionResult<RolePermissionEntity?> {
        request.rolePermissionId?.let {
            return rolePermissionService.getRolePermissionById(request)
        }

        return ActionResult<RolePermissionEntity?>()
            .withStatus(ActionStatus.NULL_OR_EMPTY_FIELD)
            .withLogMessage("Invalid request field!")
    }

    fun getRolePermissionList(request: GetRolePermissionRequestDTO): IActionResult<RolePermissionListResponseDTO> {
        return when {
            request.roleId != null -> rolePermissionService.getRolePermissionListByRoleId(request)
            request.permissionId != null -> rolePermissionService.getRolePermissionListByPermissionId(request)

            else -> ActionResult<RolePermissionListResponseDTO>()
                .withStatus(ActionStatus.NULL_OR_EMPTY_FIELD)
                .withLogMessage("Invalid request field!")
        }
    }

    fun getChildPermissionList(request: GetRolePermissionRequestDTO): IActionResult<RolePermissionListResponseDTO> {
        request.parentRolePermissionId?.let {
            return rolePermissionService.getChildRolePermissionList(request)
        }

        return ActionResult<RolePermissionListResponseDTO>()
            .withStatus(ActionStatus.NULL_OR_EMPTY_FIELD)
            .withLogMessage("Invalid request field!")
    }
}