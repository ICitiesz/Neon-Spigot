package com.islandstudio.neon.apinew.facade.player.impl

import com.islandstudio.neon.apinew.dto.ResultProvider
import com.islandstudio.neon.apinew.dto.action.CreatePlayerProfileActionDTO
import com.islandstudio.neon.apinew.entity.PlayerProfile
import com.islandstudio.neon.apinew.facade.player.IPlayerProfileFacade
import com.islandstudio.neon.apinew.service.IPlayerProfileService
import com.islandstudio.neon.shared.core.di.IComponentProvider
import com.islandstudio.neon.shared.core.di.getComponent
import org.koin.core.annotation.Single
import java.util.*

@Single
class PlayerProfileFacade: IPlayerProfileFacade, IComponentProvider {
    private val playerProfileService = getComponent<IPlayerProfileService>()

    override suspend fun createPlayerProfile(action: CreatePlayerProfileActionDTO): ResultProvider<PlayerProfile> {
        return playerProfileService.createPlayerProfile(action)
    }

    override suspend fun getPlayerProfile(uuid: UUID): ResultProvider<PlayerProfile> {
        return playerProfileService.getPlayerProfile(uuid)
    }
}