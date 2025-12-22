package com.islandstudio.neon.apinew.repository.player

import com.islandstudio.neon.apinew.connection.UserContext
import com.islandstudio.neon.apinew.entity.player.PlayerRole

interface IPlayerRoleRepository {
    suspend fun addPlayerRole(context: UserContext, playerRole: PlayerRole): PlayerRole?
    suspend fun getAll(): ArrayList<PlayerRole>
}