package com.islandstudio.neon.apinew.facade.player.impl

import com.islandstudio.neon.apinew.connection.UserContext
import com.islandstudio.neon.apinew.dto.ResultProvider
import com.islandstudio.neon.apinew.dto.action.CreatePlayerRoleActionDTO
import com.islandstudio.neon.apinew.entity.player.PlayerRole
import com.islandstudio.neon.apinew.facade.player.IPlayerRoleFacade
import com.islandstudio.neon.apinew.service.player.IPlayerRoleService
import com.islandstudio.neon.shared.core.di.IComponentProvider
import com.islandstudio.neon.shared.core.di.getComponent
import org.bukkit.entity.Player
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
class PlayerRoleFacade(@InjectedParam player: Player? = null): IPlayerRoleFacade, IComponentProvider {
    private val userContext = UserContext(player)
    private val playerRoleService = getComponent<IPlayerRoleService>()

    override suspend fun createPlayerRole(action: CreatePlayerRoleActionDTO): ResultProvider<PlayerRole> {
        return playerRoleService.createPlayerRole(userContext, action)
    }

    override suspend fun getAllPlayerRoles(): ResultProvider<ArrayList<PlayerRole>> {
        return playerRoleService.getAllPlayerRoles()
    }
}