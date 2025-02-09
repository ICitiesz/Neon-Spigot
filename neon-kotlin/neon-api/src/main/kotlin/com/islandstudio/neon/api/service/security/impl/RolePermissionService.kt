package com.islandstudio.neon.api.service.security.impl

import com.islandstudio.neon.api.dto.action.ActionResult
import com.islandstudio.neon.api.dto.action.ActionStatus
import com.islandstudio.neon.api.dto.action.IActionResult
import com.islandstudio.neon.api.dto.request.security.permission.GetRolePermissionRequestDTO
import com.islandstudio.neon.api.dto.request.security.permission.GrantRolePermissionRequestDTO
import com.islandstudio.neon.api.dto.request.security.permission.RevokeRolePermissionRequestDTO
import com.islandstudio.neon.api.dto.response.security.RolePermissionListResponseDTO
import com.islandstudio.neon.api.entity.security.RolePermissionEntity
import com.islandstudio.neon.api.repository.security.IPermissionRepository
import com.islandstudio.neon.api.repository.security.IRolePermissionRepository
import com.islandstudio.neon.api.repository.security.IRoleRepository
import com.islandstudio.neon.api.service.security.IRolePermissionService
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.exception.NeonAPIException
import org.koin.core.component.inject

class RolePermissionService: IRolePermissionService, IComponentInjector {
    private val rolePermissionRepository by inject<IRolePermissionRepository>()
    private val permissionRepository by inject<IPermissionRepository>()
    private val roleRepository by inject<IRoleRepository>()

    override fun addRolePermission(invoker: String?, request: GrantRolePermissionRequestDTO): IActionResult<RolePermissionEntity?> {
        val actionResult = ActionResult<RolePermissionEntity?>()

        runCatching {
            /* Check if the role permission exist */
            if (rolePermissionRepository.existByRoleIdPermissionId(request.roleId, request.permissionId)) {
                return actionResult
                    .withStatus(ActionStatus.ROLE_PERMISSION_EXIST)
            }

            /* Check if the role exist */
            if (!roleRepository.existById(request.roleId)) {
                return actionResult
                    .withStatus(ActionStatus.ROLE_NOT_EXIST)
            }

            /* Check if the permission exist */
            if (!permissionRepository.existById(request.permissionId)) {
                return actionResult
                    .withStatus(ActionStatus.PERMISSION_NOT_EXIST)
            }

            request.parentRolePermissionId?.let {
                /* Check if the parent role permission exist when applicable */
                if (!permissionRepository.existById(it)) {
                    return actionResult
                        .withStatus(ActionStatus.PARENT_ROLE_PERMISSION_NOT_EXIST)
                }
            }

            val rolePermission = RolePermissionEntity(
                roleId = request.roleId,
                permissionId = request.permissionId,
                parentRolePermissionId = request.parentRolePermissionId
            ).updateCreatedModified(invoker)

            rolePermissionRepository.addRolePermission(rolePermission).run {
                return actionResult
                    .withSuccessStatus()
                    .withResult(this)
            }
        }.getOrElse {
            return actionResult
                .withFailureStatus()
                .withNeonException(NeonAPIException(it.message, it))
        }
    }

    override fun getRolePermissionById(request: GetRolePermissionRequestDTO): IActionResult<RolePermissionEntity?> {
        val actionResult = ActionResult<RolePermissionEntity?>()

        runCatching {
            rolePermissionRepository.getById(request.rolePermissionId!!)?.let {
                return actionResult
                    .withSuccessStatus()
                    .withResult(it)
            }

            return actionResult
                .withStatus(ActionStatus.ROLE_PERMISSION_NOT_EXIST)
        }.getOrElse {
            return actionResult
                .withFailureStatus()
                .withNeonException(NeonAPIException(it.message, it))
        }
    }

    override fun getRolePermissionListByRoleId(request: GetRolePermissionRequestDTO): IActionResult<RolePermissionListResponseDTO> {
        val actionResult = ActionResult<RolePermissionListResponseDTO>()

        runCatching {
            val result = RolePermissionListResponseDTO(
                rolePermissionList = rolePermissionRepository.getByRoleId(request.roleId!!)
            )

            return actionResult
                .withSuccessStatus()
                .withResult(result)
        }.getOrElse {
            return actionResult
                .withFailureStatus()
                .withNeonException(NeonAPIException(it.message, it))
        }
    }

    override fun getRolePermissionListByPermissionId(request: GetRolePermissionRequestDTO): IActionResult<RolePermissionListResponseDTO> {
        val actionResult = ActionResult<RolePermissionListResponseDTO>()

        runCatching {
            val result = RolePermissionListResponseDTO(
                rolePermissionList = rolePermissionRepository.getByPermissionId(request.roleId!!)
            )

            return actionResult
                .withSuccessStatus()
                .withResult(result)
        }.getOrElse {
            return actionResult
                .withFailureStatus()
                .withNeonException(NeonAPIException(it.message, it))
        }
    }

    override fun getChildRolePermissionList(request: GetRolePermissionRequestDTO): IActionResult<RolePermissionListResponseDTO> {
        val actionResult = ActionResult<RolePermissionListResponseDTO>()

        runCatching {
            if (!rolePermissionRepository.existById(request.parentRolePermissionId!!)) {
                return actionResult
                    .withStatus(ActionStatus.PARENT_ROLE_PERMISSION_NOT_EXIST)
            }

            val result = RolePermissionListResponseDTO(
                rolePermissionRepository.getChildRolePermissions(request.parentRolePermissionId)
            )

            return actionResult
                .withSuccessStatus()
                .withResult(result)
        }.getOrElse {
            return actionResult
                .withFailureStatus()
                .withNeonException(NeonAPIException(it.message, it))
        }
    }

    override fun removeRolePermissionById(request: RevokeRolePermissionRequestDTO): IActionResult<Int> {
        val actionResult = ActionResult<Int>()

        runCatching {
            if (!rolePermissionRepository.existById(request.rolePermissionId!!)) {
                return actionResult
                    .withStatus(ActionStatus.ROLE_PERMISSION_NOT_EXIST)
            }

            return actionResult
                .withSuccessStatus()
                .withResult(rolePermissionRepository.deleteById(request.rolePermissionId))
        }.getOrElse {
            return actionResult
                .withFailureStatus()
                .withNeonException(NeonAPIException(it.message, it))
        }
    }

    override fun removeRolePermissionByRoleId(request: RevokeRolePermissionRequestDTO): IActionResult<Int> {
        val actionResult = ActionResult<Int>()

        runCatching {
            return actionResult
                .withSuccessStatus()
                .withResult(rolePermissionRepository.deleteByRoleId(request.roleId!!))
        }.getOrElse {
            return actionResult
                .withFailureStatus()
                .withNeonException(NeonAPIException(it.message, it))
        }
    }
}