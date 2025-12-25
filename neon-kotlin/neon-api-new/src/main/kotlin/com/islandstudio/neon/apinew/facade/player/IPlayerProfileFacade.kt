package com.islandstudio.neon.apinew.facade.player

import com.islandstudio.neon.apinew.dto.ResultProvider
import com.islandstudio.neon.apinew.dto.action.player.profile.CreatePlayerProfileActionDTO
import com.islandstudio.neon.apinew.entity.player.PlayerProfile
import java.util.*

interface IPlayerProfileFacade {
    suspend fun createPlayerProfile(action: CreatePlayerProfileActionDTO): ResultProvider<PlayerProfile>
    suspend fun getPlayerProfile(uuid: UUID): ResultProvider<PlayerProfile>
}