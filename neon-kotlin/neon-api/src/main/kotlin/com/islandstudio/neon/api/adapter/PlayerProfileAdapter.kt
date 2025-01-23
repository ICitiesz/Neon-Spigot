package com.islandstudio.neon.api.adapter

import com.islandstudio.neon.api.dto.action.ActionResult
import com.islandstudio.neon.api.dto.action.ActionStatus
import com.islandstudio.neon.api.dto.action.IActionResult
import com.islandstudio.neon.api.dto.request.player.CreatePlayerProfileRequestDTO
import com.islandstudio.neon.api.dto.request.player.GetPlayerProfileRequestDTO
import com.islandstudio.neon.api.dto.request.player.UpdatePlayerProfileRequestDTO
import com.islandstudio.neon.api.dto.request.security.AssignRoleRequestDTO
import com.islandstudio.neon.api.dto.request.security.UnassignRoleRequestDTO
import com.islandstudio.neon.api.entity.player.PlayerProfileEntity
import com.islandstudio.neon.api.service.IPlayerProfileService
import com.islandstudio.neon.shared.core.di.IComponentInjector
import org.koin.core.annotation.Single
import org.koin.core.component.inject

@Single
class PlayerProfileAdapter: IComponentInjector {
    private val playerProfileService by inject<IPlayerProfileService>()

    fun createPlayerProfile(request: CreatePlayerProfileRequestDTO): IActionResult<Unit> {
        return playerProfileService.createPlayerProfile(request)
    }

    fun updatePlayerProfile(request: UpdatePlayerProfileRequestDTO): IActionResult<Unit> {
        return playerProfileService.updatePlayerProfile(request)
    }

    fun getPlayerProfile(request: GetPlayerProfileRequestDTO): IActionResult<PlayerProfileEntity?> {
        /* Validate either 1 of both field is not null or empty */
        return when {
            request.playerUuid != null -> {
                playerProfileService.getPlayerProfileByUuid(request)
            }

            !request.playerName.isNullOrEmpty() -> {
                playerProfileService.getPlayerProfileByName(request)
            }

            else -> {
                ActionResult(
                    status = ActionStatus.Failure,
                    logMessage = "Error while trying to get player profile: Player UUID/Name is null or empty!"
                )
            }
        }
    }

    fun getAllPlayerProfile(): IActionResult<List<PlayerProfileEntity>> {
        return playerProfileService.getAllPlayerProfile()
    }

    fun assignRole(request: AssignRoleRequestDTO): IActionResult<Unit> {
        return playerProfileService.assignRole(request)
    }

    fun unassignRole(request: UnassignRoleRequestDTO): IActionResult<Unit> {
        return playerProfileService.unassignRole(request)
    }
}