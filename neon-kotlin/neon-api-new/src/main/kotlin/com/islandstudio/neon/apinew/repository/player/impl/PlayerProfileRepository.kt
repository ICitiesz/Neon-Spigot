package com.islandstudio.neon.apinew.repository.player.impl

import com.islandstudio.neon.apinew.connection.NeonDatabaseContext
import com.islandstudio.neon.apinew.entity.PlayerProfile
import com.islandstudio.neon.apinew.repository.BaseRepository
import com.islandstudio.neon.apinew.repository.player.IPlayerProfileRepository
import com.islandstudio.neon.apinew.schema.tables.PlayerProfileTable
import com.islandstudio.neon.apinew.schema.tables.references.PLAYER_PROFILE_TABLE
import org.koin.core.annotation.Single
import kotlin.jvm.optionals.getOrNull

@Single
class PlayerProfileRepository(databaseContext: NeonDatabaseContext): BaseRepository<PlayerProfile, PlayerProfileTable>(databaseContext, PLAYER_PROFILE_TABLE), IPlayerProfileRepository {
    override suspend fun addPlayerProfile(playerProfile: PlayerProfile): PlayerProfile? {
        return insertEntityReturnAsync(playerProfile.updateCreatedModified(playerProfile.name))
            .getOrNull()
    }

    override suspend fun getPlayerProfile(uuid: String): PlayerProfile? {
        return getSingleEntityAsync(PlayerProfile::class.java) {
            it.apply {
                add(entityTable.UUID.eq(uuid))
            }
        }.getOrNull()
    }
}