package com.islandstudio.neon.apinew.facade.player

import com.islandstudio.neon.apinew.dto.ResultProvider
import com.islandstudio.neon.apinew.dto.action.CreatePlayerRoleActionDTO
import com.islandstudio.neon.apinew.entity.player.PlayerRole

interface IPlayerRoleFacade {
    suspend fun createPlayerRole(action: CreatePlayerRoleActionDTO): ResultProvider<PlayerRole>
    suspend fun getAllPlayerRoles(): ResultProvider<ArrayList<PlayerRole>>
}