package com.islandstudio.neon.api.service

import com.islandstudio.neon.api.dto.action.IActionResult
import com.islandstudio.neon.api.dto.request.player.CreatePlayerProfileRequestDTO
import com.islandstudio.neon.api.dto.request.player.GetPlayerProfileRequestDTO
import com.islandstudio.neon.api.dto.request.player.UpdatePlayerProfileRequestDTO
import com.islandstudio.neon.api.dto.request.security.AssignRoleRequestDTO
import com.islandstudio.neon.api.dto.request.security.UnassignRoleRequestDTO
import com.islandstudio.neon.api.entity.player.PlayerProfileEntity

interface IPlayerProfileService {
    fun createPlayerProfile(request: CreatePlayerProfileRequestDTO): IActionResult<Unit>

    fun updatePlayerProfile(request: UpdatePlayerProfileRequestDTO): IActionResult<Unit>

    fun getPlayerProfileByUuid(request: GetPlayerProfileRequestDTO): IActionResult<PlayerProfileEntity?>

    fun getPlayerProfileByName(request: GetPlayerProfileRequestDTO): IActionResult<PlayerProfileEntity?>

    fun getAllPlayerProfile(): IActionResult<List<PlayerProfileEntity>>

    fun hasPlayerProfile(request: GetPlayerProfileRequestDTO): Boolean

    fun assignRole(request: AssignRoleRequestDTO): IActionResult<Unit>

    fun unassignRole(request: UnassignRoleRequestDTO): IActionResult<Unit>
}