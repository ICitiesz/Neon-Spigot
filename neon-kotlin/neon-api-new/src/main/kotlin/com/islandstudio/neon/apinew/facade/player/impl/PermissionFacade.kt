package com.islandstudio.neon.apinew.facade.player.impl

import com.islandstudio.neon.apinew.connection.UserContext
import com.islandstudio.neon.apinew.dto.ResultProvider
import com.islandstudio.neon.apinew.dto.action.player.permission.AddPermissionListActionDTO
import com.islandstudio.neon.apinew.entity.player.Permission
import com.islandstudio.neon.apinew.facade.player.IPermissionFacade
import com.islandstudio.neon.apinew.service.player.IPermissionService
import com.islandstudio.neon.shared.core.di.IComponentProvider
import com.islandstudio.neon.shared.core.di.getComponent
import org.bukkit.entity.Player
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
class PermissionFacade(@InjectedParam player: Player?): IPermissionFacade, IComponentProvider {
    private val userContext = UserContext(player)
    private val permissionService = getComponent<IPermissionService>()

    override suspend fun addPermissionList(action: AddPermissionListActionDTO): ResultProvider<Int> {
        return permissionService.addPermissionList(userContext, action)
    }

    override suspend fun removePermissionListByIds(ids: ArrayList<Long>): ResultProvider<Boolean> {
        return permissionService.removePermissionListByIds(ids)
    }

    override suspend fun getAllPermissions(): ResultProvider<ArrayList<Permission>> {
        return permissionService.getAllPermissions()
    }

}