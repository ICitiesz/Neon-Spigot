package com.islandstudio.neon.apinew.facade.player.impl

import com.islandstudio.neon.apinew.connection.UserContext
import com.islandstudio.neon.apinew.dto.ResultProvider
import com.islandstudio.neon.apinew.dto.action.CreatePlayerProfileActionDTO
import com.islandstudio.neon.apinew.entity.PlayerProfile
import com.islandstudio.neon.apinew.facade.player.IPlayerProfileFacade
import com.islandstudio.neon.apinew.service.IPlayerProfileService
import com.islandstudio.neon.shared.core.di.IComponentProvider
import com.islandstudio.neon.shared.core.di.getComponent
import org.bukkit.entity.Player
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import java.util.*

@Factory
class PlayerProfileFacade(@InjectedParam player: Player? = null): IPlayerProfileFacade, IComponentProvider {
    private val userContext = UserContext(player)
    private val playerProfileService = getComponent<IPlayerProfileService>(UserContext(player))

    override suspend fun createPlayerProfile(action: CreatePlayerProfileActionDTO): ResultProvider<PlayerProfile> {
        return playerProfileService.createPlayerProfile(userContext, action)
    }

    override suspend fun getPlayerProfile(uuid: UUID): ResultProvider<PlayerProfile> {
        return playerProfileService.getPlayerProfile(uuid)
    }
}