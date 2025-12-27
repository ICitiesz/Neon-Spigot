package com.islandstudio.neon.apinew.facade.player.impl

import com.islandstudio.neon.apinew.connection.UserContext
import com.islandstudio.neon.apinew.dto.ResultProvider
import com.islandstudio.neon.apinew.dto.action.player.profile.CreatePlayerProfileActionDTO
import com.islandstudio.neon.apinew.entity.player.PlayerProfile
import com.islandstudio.neon.apinew.facade.player.IPlayerProfileFacade
import com.islandstudio.neon.apinew.service.player.IPlayerProfileService
import com.islandstudio.neon.apinew.service.player.IPlayerRoleService
import com.islandstudio.neon.apinew.status.ActionResultStatus
import com.islandstudio.neon.shared.core.di.IComponentProvider
import com.islandstudio.neon.shared.core.di.getComponent
import org.bukkit.entity.Player
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Factory
class PlayerProfileFacade(@InjectedParam player: Player?): IPlayerProfileFacade, IComponentProvider {
    private val userContext = UserContext(player)
    private val playerProfileService = getComponent<IPlayerProfileService>()
    private val playerRoleService = getComponent<IPlayerRoleService>()

    override suspend fun createPlayerProfile(action: CreatePlayerProfileActionDTO): ResultProvider<PlayerProfile> {
        return playerProfileService.createPlayerProfile(userContext, action)
    }

    override suspend fun getPlayerProfile(uuid: UUID): ResultProvider<PlayerProfile> {
        return playerProfileService.getPlayerProfile(uuid)
    }

    override suspend fun assignPlayerRole(uuid: UUID, roleCode: String): ResultProvider<PlayerProfile> {
        val playerProfile = playerProfileService.getPlayerProfile(uuid).getResult().getOrNull()
            ?: return ResultProvider(ActionResultStatus.PlayerProfileNotExist())

        val playerRole = playerRoleService.getPlayerRoleByCode(roleCode).getResult().getOrNull()
            ?: return ResultProvider(ActionResultStatus.PlayerRoleNotExist())

        return playerProfileService.assignPlayerRole(userContext, playerProfile, playerRole)
    }

    override suspend fun unassignPlayerRole(uuid: UUID): ResultProvider<PlayerProfile> {
        TODO("Not yet implemented")
    }
}