package com.islandstudio.neon.apinew.facade.player

import com.islandstudio.neon.apinew.dto.ResultProvider
import com.islandstudio.neon.apinew.dto.action.player.role.CreatePlayerRoleActionDTO
import com.islandstudio.neon.apinew.dto.action.player.role.UpdatePlayerRoleActionDTO
import com.islandstudio.neon.apinew.entity.player.PlayerRole

interface IPlayerRoleFacade {
    suspend fun createPlayerRole(action: CreatePlayerRoleActionDTO): ResultProvider<PlayerRole>
    suspend fun removePlayerRole(code: String): ResultProvider<Boolean>
    suspend fun updatePlayerRole(action: UpdatePlayerRoleActionDTO): ResultProvider<PlayerRole>
    suspend fun getPlayerRole(code: String): ResultProvider<PlayerRole>
    suspend fun getAllPlayerRoles(): ResultProvider<ArrayList<PlayerRole>>
}