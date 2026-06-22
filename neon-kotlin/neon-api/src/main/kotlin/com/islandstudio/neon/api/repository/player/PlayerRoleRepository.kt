package com.islandstudio.neon.api.repository.player

import com.islandstudio.neon.api.dto.player.CreatePlayerRoleDto
import com.islandstudio.neon.api.dto.player.PlayerProfileDto
import com.islandstudio.neon.api.dto.player.PlayerRoleDto
import com.islandstudio.neon.api.dto.player.UpdatePlayerRoleDto

interface PlayerRoleRepository {
    suspend fun addPlayerRole(createPlayerRoleDto: CreatePlayerRoleDto): PlayerRoleDto?
    suspend fun updatePlayerRole(updatePlayerRoleDto: UpdatePlayerRoleDto): PlayerProfileDto?
    suspend fun findById(id: Long): PlayerRoleDto?
    suspend fun findByCode(code: String): PlayerRoleDto?
    suspend fun findAllWithPermission(): List<PlayerRoleDto> // May need to revise
    suspend fun deleteByCode(code: String): Boolean
}