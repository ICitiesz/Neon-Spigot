package com.islandstudio.neon.apinew.service

import com.islandstudio.neon.apinew.connection.UserContext
import com.islandstudio.neon.apinew.dto.ResultProvider
import com.islandstudio.neon.apinew.dto.action.CreatePlayerProfileActionDTO
import com.islandstudio.neon.apinew.entity.PlayerProfile
import java.util.*

interface IPlayerProfileService {
    suspend fun createPlayerProfile(context: UserContext, action: CreatePlayerProfileActionDTO): ResultProvider<PlayerProfile>
    suspend fun getPlayerProfile(uuid: UUID): ResultProvider<PlayerProfile>
}