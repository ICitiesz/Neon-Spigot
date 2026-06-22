package com.islandstudio.neon.api.provider.player

import com.islandstudio.neon.api.context.CoroutineUserContext
import com.islandstudio.neon.api.context.UserContext
import com.islandstudio.neon.api.dto.player.CreatePlayerProfileDto
import com.islandstudio.neon.api.dto.player.UpdatePlayerProfileDto
import com.islandstudio.neon.api.result.CreatePlayerProfileResult
import com.islandstudio.neon.api.result.UpdatePlayerProfileResult
import com.islandstudio.neon.api.service.player.PlayerProfileService
import com.islandstudio.neon.shared.rework.core.di.IComponentProvider
import com.islandstudio.neon.shared.rework.core.di.getComponent
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single

@Single
class PlayerProfileProvider : IComponentProvider {
    private val playerProfileService = getComponent<PlayerProfileService>()

    suspend fun createPlayerProfile(
        userContext: UserContext,
        createPlayerProfileDto: CreatePlayerProfileDto,
    ): CreatePlayerProfileResult {
        return withContext(CoroutineUserContext(userContext)) {
            playerProfileService.createPlayerProfile(createPlayerProfileDto)
        }
    }

    suspend fun updatePlayerProfile(
        userContext: UserContext,
        updatePlayerProfileDto: UpdatePlayerProfileDto,
    ): UpdatePlayerProfileResult {
        return withContext(CoroutineUserContext(userContext)) {
            playerProfileService.updatePlayerProfile(updatePlayerProfileDto)
        }
    }
}