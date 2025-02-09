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

    fun createPlayerProfile(request: CreatePlayerProfileRequestDTO): IActionResult<PlayerProfileEntity?> {
        return playerProfileService.createPlayerProfile(request,)
    }

    fun updatePlayerProfile(invoker: String?, request: UpdatePlayerProfileRequestDTO): IActionResult<PlayerProfileEntity?> {
        return playerProfileService.updatePlayerProfile(invoker, request)
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
                ActionResult<PlayerProfileEntity?>()
                    .withStatus(ActionStatus.NULL_OR_EMPTY_FIELD)
                    .withLogMessage("Error while trying to get player profile: Player UUID/Name is null or empty!")
            }
        }
    }

    fun getAllPlayerProfile(): IActionResult<List<PlayerProfileEntity>> {
        return playerProfileService.getAllPlayerProfile()
    }

    fun assignRole(invoker: String?, request: AssignRoleRequestDTO): IActionResult<Long?> {
        return playerProfileService.assignRole(invoker, request)
    }

    fun unassignRole(invoker: String?, request: UnassignRoleRequestDTO): IActionResult<Unit> {
        return playerProfileService.unassignRole(invoker, request)
    }
}