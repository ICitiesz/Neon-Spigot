package com.islandstudio.neon.apinew.service.player

import com.islandstudio.neon.apinew.connection.UserContext
import com.islandstudio.neon.apinew.dto.ResultProvider
import com.islandstudio.neon.apinew.dto.action.CreatePlayerRoleActionDTO
import com.islandstudio.neon.apinew.entity.player.PlayerRole

interface IPlayerRoleService {
    suspend fun createPlayerRole(context: UserContext, action: CreatePlayerRoleActionDTO): ResultProvider<PlayerRole>
    suspend fun getAllPlayerRoles(): ResultProvider<ArrayList<PlayerRole>>
}