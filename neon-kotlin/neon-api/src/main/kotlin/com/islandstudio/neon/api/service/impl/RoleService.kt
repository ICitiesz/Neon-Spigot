package com.islandstudio.neon.api.service.impl

import com.islandstudio.neon.api.dto.action.ActionResult
import com.islandstudio.neon.api.dto.action.ActionStatus
import com.islandstudio.neon.api.dto.action.IActionResult
import com.islandstudio.neon.api.dto.request.security.CreateRoleRequestDTO
import com.islandstudio.neon.api.dto.request.security.role.GetRoleRequestDTO
import com.islandstudio.neon.api.dto.request.security.role.RemoveRoleRequestDTO
import com.islandstudio.neon.api.entity.security.RoleEntity
import com.islandstudio.neon.api.repository.security.IRoleRepository
import com.islandstudio.neon.api.service.IRoleService
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.exception.NeonAPIException
import org.koin.core.annotation.Single
import org.koin.core.component.inject

@Single
class RoleService: IRoleService, IComponentInjector {
    private val roleRepository by inject<IRoleRepository>()

    override fun createRole(invoker: String?, request: CreateRoleRequestDTO): IActionResult<RoleEntity?> {
        val actionResult = ActionResult<RoleEntity?>()

        runCatching {
            /* Check if the role exist */
            if (roleRepository.existByRoleCode(request.roleCode)) {
                return actionResult
                    .withStatus(ActionStatus.ROLE_EXIST)
            }

            val role = RoleEntity(
                roleDisplayName = request.roleDisplayName,
                roleCode = request.roleCode
            ).updateCreatedModified(invoker)

            roleRepository.addRole(role).run {
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

    override fun getRoleById(request: GetRoleRequestDTO): IActionResult<RoleEntity?> {
        val actionResult = ActionResult<RoleEntity?>()

        runCatching {
             roleRepository.getById(request.roleId!!)?.let {
                return actionResult
                    .withSuccessStatus()
                    .withResult(it)
            }

            return actionResult.withStatus(ActionStatus.ROLE_NOT_EXIST)
        }.getOrElse {
            return actionResult
                .withFailureStatus()
                .withNeonException(NeonAPIException(it.message, it))
        }
    }

    override fun getRoleByRoleCode(request: GetRoleRequestDTO): IActionResult<RoleEntity?> {
        val actionResult = ActionResult<RoleEntity?>()

        runCatching {
            roleRepository.getByRoleCode(request.roleCode!!)?.let {
                return actionResult
                    .withSuccessStatus()
                    .withResult(it)
            }

            return actionResult.withStatus(ActionStatus.ROLE_NOT_EXIST)
        }.getOrElse {
            return actionResult
                .withFailureStatus()
                .withNeonException(NeonAPIException(it.message, it))
        }
    }

    override fun removeRoleByRoleCode(request: RemoveRoleRequestDTO): IActionResult<Int> {
        val actionResult = ActionResult<Int>()

        runCatching {
            return actionResult
                .withSuccessStatus()
                .withResult(roleRepository.deleteByRoleCode(request.roleCode))
        }.getOrElse {
            return actionResult
                .withFailureStatus()
                .withNeonException(NeonAPIException(it.message, it))
        }
    }
}