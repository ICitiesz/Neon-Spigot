package com.islandstudio.neon.apinew.service.player.impl

import com.islandstudio.neon.apinew.connection.UserContext
import com.islandstudio.neon.apinew.dto.ResultProvider
import com.islandstudio.neon.apinew.dto.action.CreatePlayerRoleActionDTO
import com.islandstudio.neon.apinew.entity.player.PlayerRole
import com.islandstudio.neon.apinew.repository.player.IPlayerRoleRepository
import com.islandstudio.neon.apinew.service.player.IPlayerRoleService
import com.islandstudio.neon.apinew.status.ActionResultStatus
import com.islandstudio.neon.shared.core.di.IComponentProvider
import com.islandstudio.neon.shared.core.di.getComponent
import org.koin.core.annotation.Single

@Single
class PlayerRoleService: IPlayerRoleService, IComponentProvider {
    private val playerRoleRepository = getComponent<IPlayerRoleRepository>()
    override suspend fun createPlayerRole(context: UserContext, action: CreatePlayerRoleActionDTO): ResultProvider<PlayerRole> {
        val result = playerRoleRepository.addPlayerRole(context, PlayerRole(
               displayName = action.displayName,
               code = action.code
            )
        )

        return ResultProvider(
            status = if (result != null) ActionResultStatus.Success() else ActionResultStatus.FailedToCreatePlayerRole(),
            result = result
        )
    }

    override suspend fun getAllPlayerRoles(): ResultProvider<ArrayList<PlayerRole>> {
        return ResultProvider(
            status = ActionResultStatus.Success(),
            result = playerRoleRepository.getAll()
        )
    }

}