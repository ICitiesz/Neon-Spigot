package com.islandstudio.neon.apinew.repository.player

import com.islandstudio.neon.apinew.connection.UserContext
import com.islandstudio.neon.apinew.entity.player.PlayerProfile

interface IPlayerProfileRepository {
    suspend fun addPlayerProfile(context: UserContext, playerProfile: PlayerProfile): PlayerProfile?
    suspend fun getPlayerProfile(uuid: String): PlayerProfile?
    suspend fun updatePlayerProfile(context: UserContext, playerProfile: PlayerProfile): PlayerProfile?
}