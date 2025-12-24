package com.islandstudio.neon.apinew.facade.player.impl

import com.islandstudio.neon.apinew.connection.UserContext
import com.islandstudio.neon.apinew.dto.ResultProvider
import com.islandstudio.neon.apinew.dto.action.player.role.CreatePlayerRoleActionDTO
import com.islandstudio.neon.apinew.dto.action.player.role.UpdatePlayerRoleActionDTO
import com.islandstudio.neon.apinew.entity.player.PlayerRole
import com.islandstudio.neon.apinew.facade.player.IPlayerRoleFacade
import com.islandstudio.neon.apinew.service.player.IPlayerRoleService
import com.islandstudio.neon.apinew.status.ActionResultStatus
import com.islandstudio.neon.shared.core.di.IComponentProvider
import com.islandstudio.neon.shared.core.di.getComponent
import org.bukkit.entity.Player
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
class PlayerRoleFacade(@InjectedParam player: Player?): IPlayerRoleFacade, IComponentProvider {
    private val userContext = UserContext(player)
    private val playerRoleService = getComponent<IPlayerRoleService>()

    override suspend fun createPlayerRole(action: CreatePlayerRoleActionDTO): ResultProvider<PlayerRole> {
        return playerRoleService.createPlayerRole(userContext, action)
    }

    override suspend fun removePlayerRole(code: String): ResultProvider<Boolean> {
        return playerRoleService.removePlyaerRoleByCode(code)
    }

    override suspend fun updatePlayerRole(action: UpdatePlayerRoleActionDTO): ResultProvider<PlayerRole> {
        return if (action.newDisplayName.isNullOrEmpty() && action.newCode.isNullOrEmpty()) {
            ResultProvider(ActionResultStatus.FailedToUpdatePlayerRole("Failed to update role as both display name and code are empty!"))
        } else {
            playerRoleService.updatePlayerRole(userContext, action)
        }
    }

    override suspend fun getPlayerRole(code: String): ResultProvider<PlayerRole> {
        return playerRoleService.getPlayerRoleByCode(code)
    }

    override suspend fun getAllPlayerRoles(): ResultProvider<ArrayList<PlayerRole>> {
        return playerRoleService.getAllPlayerRoles()
    }
}