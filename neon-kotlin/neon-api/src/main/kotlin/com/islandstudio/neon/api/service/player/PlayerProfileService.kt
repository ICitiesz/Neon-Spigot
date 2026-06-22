package com.islandstudio.neon.api.service.player

import com.islandstudio.neon.api.dto.player.CreatePlayerProfileDto
import com.islandstudio.neon.api.dto.player.PlayerProfileDto
import com.islandstudio.neon.api.dto.player.UpdatePlayerProfileDto
import com.islandstudio.neon.api.repository.player.PlayerProfileRepository
import com.islandstudio.neon.api.result.CreatePlayerProfileResult
import com.islandstudio.neon.api.result.UpdatePlayerProfileResult
import com.islandstudio.neon.shared.rework.core.di.IComponentProvider
import com.islandstudio.neon.shared.rework.core.di.getComponent
import org.koin.core.annotation.Single
import java.util.*

@Single
class PlayerProfileService: IComponentProvider {
    private val playerProfileRepository = getComponent<PlayerProfileRepository>()

    suspend fun createPlayerProfile(createPlayerProfileDto: CreatePlayerProfileDto): CreatePlayerProfileResult {
        val result: CreatePlayerProfileResult = try {
            when(val playerProfile = playerProfileRepository.addPlayerProfile(createPlayerProfileDto)) {
                null -> CreatePlayerProfileResult.PlayerProfileAlreadyExists(createPlayerProfileDto.uuid.toString())
                else -> CreatePlayerProfileResult.Success(playerProfile)
            }
        } catch (ex: Exception) {
            CreatePlayerProfileResult.Error(ex.message ?: "Unknown error")
        }

        return result
    }

    suspend fun getPlayerProfile(uuid: UUID): PlayerProfileDto? {
        return playerProfileRepository.getPlayerProfile(uuid.toString())
    }

    suspend fun updatePlayerProfile(updatePlayerProfileDto: UpdatePlayerProfileDto): UpdatePlayerProfileResult {
        val result: UpdatePlayerProfileResult = try {
            when(val updatePlayerProfile = playerProfileRepository.getPlayerProfile(updatePlayerProfileDto.uuid)) {
                null -> UpdatePlayerProfileResult.PlayerProfileNotFound(updatePlayerProfileDto.uuid)
                else -> UpdatePlayerProfileResult.Success(updatePlayerProfile)
            }
        } catch (ex: Exception) {
            UpdatePlayerProfileResult.Error(ex.message ?: "Unknown error")
        }
        return result
    }
}