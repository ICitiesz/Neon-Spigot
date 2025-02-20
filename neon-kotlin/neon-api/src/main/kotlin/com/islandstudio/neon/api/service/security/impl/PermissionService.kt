package com.islandstudio.neon.api.service.security.impl

import com.islandstudio.neon.api.dto.action.ActionResult
import com.islandstudio.neon.api.dto.action.IActionResult
import com.islandstudio.neon.api.dto.request.security.permission.AddPermissionRequestDTO
import com.islandstudio.neon.api.dto.request.security.permission.BatchAddPermissionRequestDTO
import com.islandstudio.neon.api.dto.request.security.permission.BatchRemovePermissionRequestDTO
import com.islandstudio.neon.api.dto.request.security.permission.RemovePermissionRequestDTO
import com.islandstudio.neon.api.dto.response.security.PermissionListResponseDTO
import com.islandstudio.neon.api.entity.security.PermissionEntity
import com.islandstudio.neon.api.repository.security.IPermissionRepository
import com.islandstudio.neon.api.service.security.IPermissionService
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.exception.NeonAPIException
import org.koin.core.annotation.Single
import org.koin.core.component.inject

@Single
class PermissionService: IPermissionService, IComponentInjector {
    private val permissionRepository by inject<IPermissionRepository>()

    override fun addPermission(request: AddPermissionRequestDTO): IActionResult<PermissionEntity?> {
        val actionResult = ActionResult<PermissionEntity?>()

        runCatching {
            val permission = PermissionEntity(
                permissionCode = request.permissionCode,
                permissionDesc = request.permissionDescription,
                parentPermissionId = request.parentPermissionId
            ).updateCreatedModified(null)

            permissionRepository.addPermission(permission).run {
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

    override fun addPermission(request: BatchAddPermissionRequestDTO): IActionResult<PermissionListResponseDTO> {
        val actionResult = ActionResult<PermissionListResponseDTO>()

        runCatching {
            val resultList: ArrayList<PermissionEntity> = arrayListOf()

            val mainRecordList = request.permissions.map {
                PermissionEntity(
                    permissionCode = it.permissionCode,
                    permissionDesc = it.permissionDescription,
                    parentPermissionId = it.parentPermissionId
                ).updateCreatedModified(null)
            }

            permissionRepository.batchAddPermission(mainRecordList).apply {
                resultList.addAll(this)

                val subRecord = request.permissions
                    .filter { x -> x.subPermissions.isNotEmpty() }
                    .flatMap {
                        val permission = this.find { x -> x.permissionCode == it.permissionCode }

                        it.subPermissions.mapTo(arrayListOf()) {
                            PermissionEntity(
                                permissionCode = it.permissionCode,
                                permissionDesc = it.permissionDescription,
                                parentPermissionId = permission?.permissionId
                            ).updateCreatedModified(null)
                        }
                    }

                resultList.addAll(permissionRepository.batchAddPermission(subRecord))
            }

            val result = PermissionListResponseDTO(resultList)

            return actionResult
                .withSuccessStatus()
                .withResult(result)
        }.getOrElse {
            return actionResult
                .withFailureStatus()
                .withNeonException(NeonAPIException(it.message, it))
        }
    }

    override fun removePermissionById(request: RemovePermissionRequestDTO): IActionResult<Int> {
        val actionResult = ActionResult<Int>()

        runCatching {
            return actionResult
                .withSuccessStatus()
                .withResult(permissionRepository.deleteById(request.permissionId!!))
        }.getOrElse {
            return actionResult
                .withFailureStatus()
                .withNeonException(NeonAPIException(it.message, it))
        }
    }

    override fun removePermissionById(request: BatchRemovePermissionRequestDTO): IActionResult<Int> {
        val actionResult = ActionResult<Int>()

        runCatching {
            val idList = request.permissionList.map { it.permissionId!! }

            return actionResult
                .withSuccessStatus()
                .withResult(permissionRepository.batchDeleteById(idList))
        }.getOrElse {
            return actionResult
                .withFailureStatus()
                .withNeonException(NeonAPIException(it.message, it))
        }
    }

    override fun removePermissionByPermissionCode(request: RemovePermissionRequestDTO): IActionResult<Int> {
        val actionResult = ActionResult<Int>()

        runCatching {
            return actionResult
                .withSuccessStatus()
                .withResult(permissionRepository.deleteByPermissionCode(request.permissionCode!!))
        }.getOrElse {
            return actionResult
                .withFailureStatus()
                .withNeonException(NeonAPIException(it.message, it))
        }
    }

    override fun removePermissionByPermissionCode(request: BatchRemovePermissionRequestDTO): IActionResult<Int> {
        val actionResult = ActionResult<Int>()

        runCatching {
            val permissionCodeList = request.permissionList.map { it.permissionCode!! }

            return actionResult
                .withSuccessStatus()
                .withResult(permissionRepository.batchDeleteByPermissionCode(permissionCodeList))
        }.getOrElse {
            return actionResult
                .withFailureStatus()
                .withNeonException(NeonAPIException(it.message, it))
        }
    }


    override fun getAllPermission(): IActionResult<PermissionListResponseDTO> {
        val actionResult = ActionResult<PermissionListResponseDTO>()

        runCatching {
            val result = PermissionListResponseDTO(
                permissionRepository.getAll()
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


}