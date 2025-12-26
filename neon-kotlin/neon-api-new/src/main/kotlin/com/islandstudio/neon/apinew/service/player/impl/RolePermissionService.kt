package com.islandstudio.neon.apinew.service.player.impl

import com.islandstudio.neon.apinew.connection.UserContext
import com.islandstudio.neon.apinew.dto.ResultProvider
import com.islandstudio.neon.apinew.dto.action.player.permission.AddRolePermisionActionDTO
import com.islandstudio.neon.apinew.entity.player.RolePermission
import com.islandstudio.neon.apinew.repository.player.IPermissionRepository
import com.islandstudio.neon.apinew.repository.player.IPlayerRoleRepository
import com.islandstudio.neon.apinew.repository.player.IRolePermissionRepository
import com.islandstudio.neon.apinew.service.player.IRolePermissionService
import com.islandstudio.neon.apinew.status.ActionResultStatus
import com.islandstudio.neon.shared.core.di.IComponentProvider
import com.islandstudio.neon.shared.core.di.getComponent
import org.koin.core.annotation.Single

@Single
class RolePermissionService: IRolePermissionService, IComponentProvider {
    private val playerRoleRepository = getComponent<IPlayerRoleRepository>()
    private val permissionRepository = getComponent<IPermissionRepository>()
    private val rolePermissionRepository = getComponent<IRolePermissionRepository>()

    override suspend fun addRolePermissionList(context: UserContext, action: AddRolePermisionActionDTO): ResultProvider<Int> {
        val playerRole = playerRoleRepository.getSingleByCode(action.roleCode) ?: return ResultProvider(ActionResultStatus.PlayerRoleNotExist())
        val grantedRolePermissionList = rolePermissionRepository.getListByRoleId(playerRole.id!!)
        val serverPermissionList = permissionRepository.getAll()

        /* Insert parent role permission */
        val newParentRolePermissionList = serverPermissionList
            .asSequence()
            .filter { x -> x.parentId == null }
            .filter { x -> x.code in action.rolePermissionList.map { y -> y.permissionCode } }
            .filter { x -> x.id !in grantedRolePermissionList.map { y -> y.permissionId } }
            .map { x -> RolePermission(null, null, playerRole.id!!, x.id!!) }
            .toCollection(ArrayList()).run {
                rolePermissionRepository.addList(context, this,)
            }

        val newChildRolePermissionList = ArrayList<RolePermission>()

        /* Attach parentId to child and insert child */
        action.rolePermissionList.forEach { rolePermissionActionDTO ->
            val serverPermission = serverPermissionList.find { x -> x.code == rolePermissionActionDTO.permissionCode } ?: return@forEach
            val parentRolePermission = grantedRolePermissionList
                .also { it.addAll(newParentRolePermissionList) }
                .filter { x -> x.parentId ==  null }
                .find { x -> x.permissionId == serverPermission.id } ?: return@forEach

           newChildRolePermissionList.addAll(serverPermissionList
                .filter { x -> x.parentId == serverPermission.id }
                .filter { x -> x.code in rolePermissionActionDTO.subRolePermissions.map { y -> y.permissionCode } }
                .map { x -> RolePermission(null, parentRolePermission.id, playerRole.id!!, x.id!!) }
                .toCollection(ArrayList())
           )
        }

        val totalResult = rolePermissionRepository.addList(context, newChildRolePermissionList).size + newParentRolePermissionList.size

        return ResultProvider(
            status = ActionResultStatus.Success(),
            totalResult
        )
    }

    override suspend fun removeRolePermissionListByIds(ids: ArrayList<Long>): ResultProvider<Boolean> {
        return ResultProvider(
            status = ActionResultStatus.Success(),
            result = rolePermissionRepository.removeListByIds(ids)
        )
    }

    override suspend fun isRolePermissionExistByPermissionCodes(roleCode: String, permissionCodes: ArrayList<String>): ResultProvider<Boolean> {
        val result = rolePermissionRepository.getListDetailByPermissionCodes(roleCode, permissionCodes).also {
            if (it.isEmpty()) return ResultProvider(ActionResultStatus.Success(), false)
        }

        return ResultProvider(
            status = ActionResultStatus.RolePermissionAlreadyExist("Role permission already exist: ${result.joinToString(", ") { x -> x.permissionCode }}"),
            result = result.isNotEmpty()
        )
    }
}