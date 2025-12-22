package com.islandstudio.neon.apinew.repository.player.impl

import com.islandstudio.neon.apinew.connection.NeonDatabaseContext
import com.islandstudio.neon.apinew.connection.UserContext
import com.islandstudio.neon.apinew.entity.player.PlayerRole
import com.islandstudio.neon.apinew.repository.BaseRepository
import com.islandstudio.neon.apinew.repository.player.IPlayerRoleRepository
import com.islandstudio.neon.apinew.schema.tables.PlayerRoleTable
import com.islandstudio.neon.apinew.schema.tables.references.PLAYER_ROLE_TABLE
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Single
import kotlin.jvm.optionals.getOrNull

@Single
class PlayerRoleRepository(databaseContext: NeonDatabaseContext, @InjectedParam val userContext: UserContext):
    BaseRepository<PlayerRole, PlayerRoleTable>(databaseContext, PLAYER_ROLE_TABLE),
    IPlayerRoleRepository
{
    override suspend fun addPlayerRole(context: UserContext, playerRole: PlayerRole): PlayerRole? {
        return insertEntityReturnAsync(playerRole.updateCreatedModified(userContext.contextName)).getOrNull()
    }

    override suspend fun getAll(): ArrayList<PlayerRole> {
        return getAllEntitiesAsync(PlayerRole::class.java).toCollection(ArrayList())
    }
}