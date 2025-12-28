package com.islandstudio.neon.apinew.facade.player.impl

import com.islandstudio.neon.apinew.connection.UserContext
import com.islandstudio.neon.apinew.dto.ResultProvider
import com.islandstudio.neon.apinew.dto.action.player.permission.AddRolePermisionActionDTO
import com.islandstudio.neon.apinew.dto.result.player.permission.RolePermissionDetailResultDTO
import com.islandstudio.neon.apinew.facade.player.IRolePermissionFacade
import com.islandstudio.neon.apinew.service.player.IRolePermissionService
import com.islandstudio.neon.shared.core.di.IComponentProvider
import com.islandstudio.neon.shared.core.di.getComponent
import org.bukkit.entity.Player
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
class RolePermissionFacade(@InjectedParam player: Player?): IRolePermissionFacade, IComponentProvider {
    private val userContext = UserContext(player)
    private val rolePermissionService = getComponent<IRolePermissionService>()

    override suspend fun addRolePermissionList(action: AddRolePermisionActionDTO): ResultProvider<Long> {
        return rolePermissionService.addRolePermissionList(userContext, action)
    }

    override suspend fun getRolePermissionDetailList(roleId: Long): ResultProvider<ArrayList<RolePermissionDetailResultDTO>> {
        return rolePermissionService.getRolePermissionDetailListByRoleId(roleId)
    }

    override suspend fun removeRolePermissionListByIds(ids: ArrayList<Long>): ResultProvider<Boolean> {
        return rolePermissionService.removeRolePermissionListByIds(ids)
    }

    override suspend fun isRolePermissionExistByPermissionCodes(roleCode: String, permissionCodes: ArrayList<String>): ResultProvider<Boolean> {
        return rolePermissionService.isRolePermissionExistByPermissionCodes(roleCode, permissionCodes)
    }
}