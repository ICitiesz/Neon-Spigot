package com.islandstudio.neon.api.adapter.security

import com.islandstudio.neon.api.dto.action.ActionResult
import com.islandstudio.neon.api.dto.action.ActionStatus
import com.islandstudio.neon.api.dto.action.IActionResult
import com.islandstudio.neon.api.dto.request.security.permission.BatchAddPermissionRequestDTO
import com.islandstudio.neon.api.dto.request.security.permission.BatchRemovePermissionRequestDTO
import com.islandstudio.neon.api.dto.response.security.PermissionListResponseDTO
import com.islandstudio.neon.api.service.security.IPermissionService
import com.islandstudio.neon.shared.core.di.IComponentInjector
import org.koin.core.annotation.Single
import org.koin.core.component.inject

@Single
class PermissionAdaptor: IComponentInjector {
    private val permissionService by inject<IPermissionService>()

    fun addPermission(request: BatchAddPermissionRequestDTO): IActionResult<PermissionListResponseDTO> {
        return permissionService.addPermission(request)
    }

    fun removePermission(request: BatchRemovePermissionRequestDTO): IActionResult<Int> {
        return when {
            request.permissionList
                .none { it.permissionId == null } -> permissionService.removePermissionById(request)

            request.permissionList
                .none { it.permissionCode == null } -> permissionService.removePermissionByPermissionCode(request)

            else -> ActionResult<Int>()
                .withStatus(ActionStatus.INVALID_REQUEST_FIELD)
        }
    }

    fun getAllPermission(): IActionResult<PermissionListResponseDTO> {
        return permissionService.getAllPermission()
    }
}