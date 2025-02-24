package com.islandstudio.neon.api.service.security.impl

import com.islandstudio.neon.api.dto.action.ActionResult
import com.islandstudio.neon.api.dto.action.ActionStatus
import com.islandstudio.neon.api.dto.action.IActionResult
import com.islandstudio.neon.api.dto.request.security.permission.*
import com.islandstudio.neon.api.dto.response.security.RolePermissionListResponseDTO
import com.islandstudio.neon.api.entity.security.RolePermissionEntity
import com.islandstudio.neon.api.repository.security.IPermissionRepository
import com.islandstudio.neon.api.repository.security.IRolePermissionRepository
import com.islandstudio.neon.api.repository.security.IRoleRepository
import com.islandstudio.neon.api.service.security.IRolePermissionService
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.exception.NeonAPIException
import org.koin.core.annotation.Single
import org.koin.core.component.inject

@Single
class RolePermissionService: IRolePermissionService, IComponentInjector {
    private val rolePermissionRepository by inject<IRolePermissionRepository>()
    private val permissionRepository by inject<IPermissionRepository>()
    private val roleRepository by inject<IRoleRepository>()

    override fun addRolePermission(invoker: String?, request: GrantRolePermissionRequestDTO): IActionResult<RolePermissionEntity?> {
        val actionResult = ActionResult<RolePermissionEntity?>()

        runCatching {
            /* Check if the role permission exist */
            if (rolePermissionRepository.existByRoleIdPermissionId(request.roleId!!, request.permissionId)) {
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

    override fun addRolePermission(invoker: String?, request: BatchGrantRolePermissionRequestDTO): IActionResult<RolePermissionListResponseDTO> {
        val actionResult = ActionResult<RolePermissionListResponseDTO>()

        runCatching {
            val resultList: ArrayList<RolePermissionEntity> = arrayListOf()

            val mainRecordList = request.rolePermissionList
                .map {
                    RolePermissionEntity(
                        roleId = it.roleId,
                        permissionId = it.permissionId,
                        parentRolePermissionId = it.parentRolePermissionId
                    ).updateCreatedModified(invoker)
                }

            rolePermissionRepository.batchAddRolePermission(mainRecordList).apply {
                resultList.addAll(this)

                val subRecordList = this.flatMap { mainRolePermissionEntity ->
                    val mainPermission = request.rolePermissionList
                        .filter { x -> x.subRolePermissions.isNotEmpty() }
                        .find { x -> x.permissionId == mainRolePermissionEntity.permissionId } ?: return@apply

                    mainPermission.subRolePermissions.mapTo(arrayListOf()) {
                        RolePermissionEntity(
                            roleId = it.roleId,
                            permissionId = it.permissionId,
                            parentRolePermissionId = mainRolePermissionEntity.rolePermissionId
                        ).updateCreatedModified(invoker)
                    }
                }

                resultList.addAll(rolePermissionRepository.batchAddRolePermission(subRecordList))
            }

            val result = RolePermissionListResponseDTO(
                rolePermissionList = resultList
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
            val result = if (request.includePermissionCode) {
                RolePermissionListResponseDTO(
                    rolePermissionPermissionCodeList = rolePermissionRepository.getWithPermissionCodeByRoleId(request.roleId!!)
                )
            } else {
                RolePermissionListResponseDTO(
                    rolePermissionList = rolePermissionRepository.getByRoleId(request.roleId!!)
                )
            }

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

    override fun removeRolePermissionById(request: BatchRevokeRolePermissionRequestDTO): IActionResult<Int> {
        val actionResult = ActionResult<Int>()

        runCatching {
            val idList = request.rolePermissionList.map { it.rolePermissionId!! }

            return actionResult
                .withSuccessStatus()
                .withResult(rolePermissionRepository.batchDeleteById(idList))
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

    override fun removkeRolePermissionByRoleId(request: BatchRevokeRolePermissionRequestDTO): IActionResult<Int> {
        val actionResult = ActionResult<Int>()

        runCatching {
            val idList = request.rolePermissionList.map { it.roleId!! }

            return actionResult
                .withSuccessStatus()
                .withResult(rolePermissionRepository.batchDeleteByRoleId(idList))
        }.getOrElse {
            return actionResult
                .withFailureStatus()
                .withNeonException(NeonAPIException(it.message, it))
        }
    }


}