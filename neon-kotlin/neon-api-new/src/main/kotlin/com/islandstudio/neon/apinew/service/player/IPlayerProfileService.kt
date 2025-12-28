package com.islandstudio.neon.apinew.service.player

import com.islandstudio.neon.apinew.connection.UserContext
import com.islandstudio.neon.apinew.dto.ResultProvider
import com.islandstudio.neon.apinew.dto.action.player.profile.CreatePlayerProfileActionDTO
import com.islandstudio.neon.apinew.entity.player.PlayerProfile
import com.islandstudio.neon.apinew.entity.player.PlayerRole
import java.util.*

interface IPlayerProfileService {
    suspend fun createPlayerProfile(context: UserContext, action: CreatePlayerProfileActionDTO): ResultProvider<PlayerProfile>
    suspend fun getPlayerProfile(uuid: UUID): ResultProvider<PlayerProfile>
    suspend fun assignPlayerRole(context: UserContext, playerProfile: PlayerProfile, playerRole: PlayerRole): ResultProvider<PlayerProfile>
    suspend fun unassignPlayerRole(context: UserContext, playerProfile: PlayerProfile): ResultProvider<PlayerProfile>
}