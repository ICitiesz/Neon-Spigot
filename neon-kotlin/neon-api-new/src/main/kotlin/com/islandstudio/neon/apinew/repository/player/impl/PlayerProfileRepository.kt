package com.islandstudio.neon.apinew.repository.player.impl

import com.islandstudio.neon.apinew.connection.ApiContext
import com.islandstudio.neon.apinew.connection.NeonDatabaseContext
import com.islandstudio.neon.apinew.entity.player.PlayerProfile
import com.islandstudio.neon.apinew.repository.BaseRepository
import com.islandstudio.neon.apinew.repository.player.IPlayerProfileRepository
import com.islandstudio.neon.apinew.schema.tables.PlayerProfileTable
import com.islandstudio.neon.apinew.schema.tables.references.PLAYER_PROFILE_TABLE
import org.koin.core.annotation.Single
import kotlin.jvm.optionals.getOrNull

@Single
class PlayerProfileRepository(databaseContext: NeonDatabaseContext):
    BaseRepository<PlayerProfile, PlayerProfileTable>(databaseContext, PLAYER_PROFILE_TABLE),
    IPlayerProfileRepository
{
    override suspend fun addPlayerProfile(context: ApiContext, playerProfile: PlayerProfile): PlayerProfile? {
        return insertEntityReturnAsync(playerProfile.updateCreatedModified(context.contextName))
            .getOrNull()
    }

    override suspend fun getPlayerProfile(uuid: String): PlayerProfile? {
        return getSingleEntityAsync(PlayerProfile::class.java) {
            add(entityTable.UUID.eq(uuid))
            this
        }.getOrNull()
    }

    override suspend fun updatePlayerProfile(context: ApiContext, playerProfile: PlayerProfile): PlayerProfile? {
        return updateEntityReturnAsync(playerProfile.updateModified(context.contextName)) {
            add(entityTable.ID.eq(playerProfile.id))
            this
        }.getOrNull()
    }
}