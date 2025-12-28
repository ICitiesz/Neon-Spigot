package com.islandstudio.neon.apinew.service.player

import com.islandstudio.neon.apinew.connection.UserContext
import com.islandstudio.neon.apinew.dto.ResultProvider
import com.islandstudio.neon.apinew.dto.action.player.role.CreatePlayerRoleActionDTO
import com.islandstudio.neon.apinew.dto.action.player.role.UpdatePlayerRoleActionDTO
import com.islandstudio.neon.apinew.entity.player.PlayerRole

interface IPlayerRoleService {
    suspend fun createPlayerRole(context: UserContext, action: CreatePlayerRoleActionDTO): ResultProvider<PlayerRole>
    suspend fun removePlyaerRoleByCode(code: String): ResultProvider<Boolean>
    suspend fun updatePlayerRole(context: UserContext, action: UpdatePlayerRoleActionDTO): ResultProvider<PlayerRole>
    suspend fun getPlayerRoleById(id: Long): ResultProvider<PlayerRole>
    suspend fun getPlayerRoleByCode(roleCode: String): ResultProvider<PlayerRole>
    suspend fun getAllPlayerRoles(): ResultProvider<ArrayList<PlayerRole>>
}