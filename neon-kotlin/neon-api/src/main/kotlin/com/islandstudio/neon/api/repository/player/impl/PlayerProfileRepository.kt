package com.islandstudio.neon.api.repository.player.impl

import com.islandstudio.neon.api.IDatabaseContext
import com.islandstudio.neon.api.entity.player.PlayerProfileEntity
import com.islandstudio.neon.api.repository.player.IPlayerProfileRepository
import com.islandstudio.neon.api.schema.neon_data.tables.records.TPlayerProfileRecord
import com.islandstudio.neon.api.schema.neon_data.tables.references.T_PLAYER_PROFILE
import com.islandstudio.neon.shared.utils.data.IObjectMapper
import org.koin.core.annotation.Single
import java.util.*

@Single
class PlayerProfileRepository: IPlayerProfileRepository, IDatabaseContext, IObjectMapper {
    override fun addPlayerProfile(playerProfileEntity: PlayerProfileEntity): PlayerProfileEntity? {
        val record = mapTo(
            playerProfileEntity.updateCreatedModified(playerProfileEntity.playerName!!),
            TPlayerProfileRecord::class.java
        )

        runCatching {
            return dbContext()
                .insertInto(T_PLAYER_PROFILE)
                .set(record)
                .returning()
                .fetchOneInto(PlayerProfileEntity::class.java)
        }.getOrThrow()
    }

    override fun updatePlayerProfile(playerProfileEntity: PlayerProfileEntity): PlayerProfileEntity? {
        val record = mapTo(
            playerProfileEntity,
            TPlayerProfileRecord::class.java
        )

        runCatching {
            return dbContext()
                .update(T_PLAYER_PROFILE)
                .set(record)
                .where(T_PLAYER_PROFILE.ID.eq(record.id))
                .returning()
                .fetchOneInto(PlayerProfileEntity::class.java)
        }.getOrThrow()
    }

    override fun getAll(): List<PlayerProfileEntity> {
        runCatching {
            return dbContext()
                .fetch(T_PLAYER_PROFILE)
                .into(PlayerProfileEntity::class.java)
        }.getOrThrow()
    }

    override fun getByUUID(uuid: UUID): PlayerProfileEntity? {
        runCatching {
            return dbContext()
                .select()
                .from(T_PLAYER_PROFILE)
                .where(T_PLAYER_PROFILE.PLAYER_UUID.eq(uuid))
                .fetchOneInto(PlayerProfileEntity::class.java)
        }.getOrThrow()
    }

    override fun getByPlayerName(playerName: String): PlayerProfileEntity? {
        runCatching {
            return dbContext()
                .select()
                .from(T_PLAYER_PROFILE)
                .where(T_PLAYER_PROFILE.PLAYER_NAME.eq(playerName))
                .fetchOneInto(PlayerProfileEntity::class.java)
        }.getOrThrow()
    }

    override fun getAssignedRoleId(uuid: UUID): Long? {
        runCatching {
            return dbContext()
                .select(T_PLAYER_PROFILE.ROLE_ID)
                .from(T_PLAYER_PROFILE)
                .where(T_PLAYER_PROFILE.PLAYER_UUID.eq(uuid))
                .fetchOne(T_PLAYER_PROFILE.ROLE_ID)
        }.getOrThrow()
    }

    override fun hasRoleAssign(uuid: UUID): Boolean {
        val sqlQuery = dbContext()
            .select()
            .from(T_PLAYER_PROFILE)
            .where(
                T_PLAYER_PROFILE.PLAYER_UUID.eq(uuid)
                .and(T_PLAYER_PROFILE.ROLE_ID.isNotNull))

        runCatching {
            return dbContext().fetchExists(sqlQuery)
        }.getOrThrow()
    }

    override fun existsByUUID(uuid: UUID): Boolean {
        val sqlQuery = dbContext()
            .select()
            .from(T_PLAYER_PROFILE)
            .where(T_PLAYER_PROFILE.PLAYER_UUID.eq(uuid))

        runCatching {
            return dbContext().fetchExists(sqlQuery)
        }.getOrThrow()
    }

    override fun existsByPlayerName(playerName: String): Boolean {
        val sqlQuery = dbContext()
            .select()
            .from(T_PLAYER_PROFILE)
            .where(T_PLAYER_PROFILE.PLAYER_NAME.eq(playerName))

        runCatching {
            return dbContext().fetchExists(sqlQuery)
        }.getOrThrow()
    }
}