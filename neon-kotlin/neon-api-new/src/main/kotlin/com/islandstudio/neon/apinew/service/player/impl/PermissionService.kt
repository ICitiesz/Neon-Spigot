package com.islandstudio.neon.apinew.service.player.impl

import com.islandstudio.neon.apinew.connection.UserContext
import com.islandstudio.neon.apinew.dto.ResultProvider
import com.islandstudio.neon.apinew.dto.action.player.permission.AddPermissionListActionDTO
import com.islandstudio.neon.apinew.entity.player.Permission
import com.islandstudio.neon.apinew.repository.player.IPermissionRepository
import com.islandstudio.neon.apinew.service.player.IPermissionService
import com.islandstudio.neon.apinew.status.ActionResultStatus
import com.islandstudio.neon.shared.core.di.IComponentProvider
import com.islandstudio.neon.shared.core.di.getComponent
import com.islandstudio.neon.shared.utils.data.ModelMapperUtil
import org.koin.core.annotation.Single

@Single
class PermissionService: IPermissionService, IComponentProvider {
    private val permissionRepository = getComponent<IPermissionRepository>()
    private val mapper = getComponent<ModelMapperUtil>()

    override suspend fun addPermissionList(context: UserContext, action: AddPermissionListActionDTO): ResultProvider<Int> {
        var resultCount = 0
        val mainPermissionList = action.permissionList.map {
            Permission(
                name = it.name,
                code = it.code,
                description = it.description,
                parentId = it.parentPermissionId
            )
        }.toCollection(ArrayList())

        permissionRepository.addPermissionList(context, mainPermissionList).apply {
            resultCount += this.size

            val subPermissionList = action.permissionList
                .filter { permission -> permission.subPermissions.isNotEmpty() }
                .flatMap {
                    val permission = this.find { x -> x.code == it.code }

                    it.subPermissions.mapTo(arrayListOf()) { subPermission ->
                        Permission(
                            name = subPermission.name,
                            code = subPermission.code,
                            description = subPermission.description,
                            parentId = permission?.id
                        )
                    }
                }.toCollection(ArrayList())

            resultCount += permissionRepository.addPermissionList(context, subPermissionList).size
        }

        return ResultProvider(
            status = ActionResultStatus.Success(),
            result = resultCount
        )
    }

    override suspend fun removePermissionListByIds(ids: ArrayList<Long>): ResultProvider<Boolean> {
        return ResultProvider(
            status = ActionResultStatus.Success(),
            result = permissionRepository.removeListById(ids)
        )
    }

    override suspend fun getAllPermissions(): ResultProvider<ArrayList<Permission>> {
        return ResultProvider(
            status = ActionResultStatus.Success(),
            result = permissionRepository.getAll()
        )
    }
}