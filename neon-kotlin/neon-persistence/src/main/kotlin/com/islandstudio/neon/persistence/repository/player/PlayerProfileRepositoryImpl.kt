package com.islandstudio.neon.persistence.repository.player

import com.islandstudio.neon.apirework.dto.player.CreatePlayerProfileDto
import com.islandstudio.neon.apirework.dto.player.PlayerProfileDto
import com.islandstudio.neon.apirework.dto.player.UpdatePlayerProfileDto
import com.islandstudio.neon.apirework.repository.player.PlayerProfileRepository
import com.islandstudio.neon.persistence.DatabaseContext
import com.islandstudio.neon.persistence.entity.player.PlayerProfile
import com.islandstudio.neon.persistence.entity.player.PlayerProfileTable
import com.islandstudio.neon.persistence.repository.BaseRepositoryImpl
import org.jetbrains.exposed.v1.core.eq
import org.koin.core.annotation.Single
import java.time.LocalDateTime

@Single
class PlayerProfileRepositoryImpl(dbContext: DatabaseContext):
    BaseRepositoryImpl<PlayerProfile, PlayerProfileTable>(dbContext, PlayerProfileTable), PlayerProfileRepository {
    override suspend fun addPlayerProfile(createPlayerProfileDto: CreatePlayerProfileDto): PlayerProfileDto? {
        val newPlayerProfile = PlayerProfile(
            uuid = createPlayerProfileDto.uuid.toString(),
            name = createPlayerProfileDto.name,
            joinedAt = LocalDateTime.now(),
        )
        val result = insertEntityReturnAsync(newPlayerProfile)

        return result?.let {
            PlayerProfileDto(
                id = it.id,
                uuid = it.uuid,
                name = it.name,
                joinedAt = it.joinedAt,
                roleId = it.roleId
            )
        }
    }

    override suspend fun getPlayerProfile(uuid: String): PlayerProfileDto? {
        val result = getSingleEntityAsync {
            table.uuid.eq(uuid)
        }

        return result?.let {
            PlayerProfileDto(
                id = it.id,
                uuid = it.uuid,
                name = it.name,
                joinedAt = it.joinedAt,
                roleId = it.roleId
            )
        }
    }

    override suspend fun updatePlayerProfile(updatePlayerProfileDto: UpdatePlayerProfileDto): PlayerProfileDto? {
        val result = updateEntityReturnAsync(updatePlayerProfileDto.id) {
            it[table.name] = updatePlayerProfileDto.name
        }

        return result?.let {
            PlayerProfileDto(
                id = it.id,
                uuid = it.uuid,
                name = it.name,
                joinedAt = it.joinedAt,
                roleId = it.roleId
            )
        }
    }
}