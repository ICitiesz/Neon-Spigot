package com.islandstudio.neon.apinew.repository.player

import com.islandstudio.neon.apinew.connection.UserContext
import com.islandstudio.neon.apinew.entity.player.PlayerRole

interface IPlayerRoleRepository {
    suspend fun addPlayerRole(context: UserContext, playerRole: PlayerRole): PlayerRole?
    suspend fun updatePlayerRole(context: UserContext, playerRole: PlayerRole): PlayerRole?
    suspend fun getSingleByCode(code: String): PlayerRole?
    suspend fun getSingleById(id: Long): PlayerRole?
    suspend fun getAll(): ArrayList<PlayerRole>
    suspend fun isExistByCode(code: String): Boolean
    suspend fun deleteByCode(code: String): Boolean
}