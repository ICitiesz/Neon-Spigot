package com.islandstudio.neon.apinew.repository.player

import com.islandstudio.neon.apinew.entity.PlayerProfile

interface IPlayerProfileRepository {
    suspend fun addPlayerProfile(playerProfile: PlayerProfile): PlayerProfile?
    suspend fun getPlayerProfile(uuid: String): PlayerProfile?
}