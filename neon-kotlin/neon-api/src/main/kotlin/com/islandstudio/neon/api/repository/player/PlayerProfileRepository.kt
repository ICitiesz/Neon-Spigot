package com.islandstudio.neon.api.repository.player

import com.islandstudio.neon.api.dto.player.CreatePlayerProfileDto
import com.islandstudio.neon.api.dto.player.PlayerProfileDto
import com.islandstudio.neon.api.dto.player.UpdatePlayerProfileDto

interface PlayerProfileRepository {
    suspend fun addPlayerProfile(createPlayerProfileDto: CreatePlayerProfileDto): PlayerProfileDto?
    suspend fun getPlayerProfile(uuid: String): PlayerProfileDto?
    suspend fun updatePlayerProfile(updatePlayerProfileDto: UpdatePlayerProfileDto): PlayerProfileDto?
}