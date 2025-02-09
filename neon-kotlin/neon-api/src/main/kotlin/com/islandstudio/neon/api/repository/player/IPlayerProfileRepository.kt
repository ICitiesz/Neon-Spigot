package com.islandstudio.neon.api.repository.player

import com.islandstudio.neon.api.entity.player.PlayerProfileEntity
import java.util.*

interface IPlayerProfileRepository {
    fun addPlayerProfile(playerProfileEntity: PlayerProfileEntity): PlayerProfileEntity?

    fun updatePlayerProfile(playerProfileEntity: PlayerProfileEntity): PlayerProfileEntity?

    fun getAll(): List<PlayerProfileEntity>

    fun getByUUID(uuid: UUID): PlayerProfileEntity?

    fun getByPlayerName(playerName: String): PlayerProfileEntity?

    fun getAssignedRoleId(uuid: UUID): Long?

    fun hasRoleAssign(uuid: UUID): Boolean

    fun existsByUUID(uuid: UUID): Boolean

    fun existsByPlayerName(playerName: String): Boolean
}