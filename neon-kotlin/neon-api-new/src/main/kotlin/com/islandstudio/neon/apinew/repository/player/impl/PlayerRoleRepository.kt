package com.islandstudio.neon.apinew.repository.player.impl

import com.islandstudio.neon.apinew.connection.NeonDatabaseContext
import com.islandstudio.neon.apinew.connection.UserContext
import com.islandstudio.neon.apinew.entity.player.PlayerRole
import com.islandstudio.neon.apinew.repository.BaseRepository
import com.islandstudio.neon.apinew.repository.player.IPlayerRoleRepository
import com.islandstudio.neon.apinew.schema.tables.PlayerRoleTable
import com.islandstudio.neon.apinew.schema.tables.references.PLAYER_ROLE_TABLE
import org.koin.core.annotation.Single
import kotlin.jvm.optionals.getOrNull

@Single
class PlayerRoleRepository(databaseContext: NeonDatabaseContext):
    BaseRepository<PlayerRole, PlayerRoleTable>(databaseContext, PLAYER_ROLE_TABLE),
    IPlayerRoleRepository
{
    override suspend fun addPlayerRole(context: UserContext, playerRole: PlayerRole): PlayerRole? {
        return insertEntityReturnAsync(playerRole.updateCreatedModified(context.contextName)).getOrNull()
    }

    override suspend fun updatePlayerRole(context: UserContext, playerRole: PlayerRole): PlayerRole? {
        return updateEntityReturnAsync(playerRole.updateModified(context.contextName)) {
            add(entityTable.ID.eq(playerRole.id))
            this
        }.getOrNull()
    }

    override suspend fun getSingleByCode(code: String): PlayerRole? {
        return getSingleEntityAsync(PlayerRole::class.java) {
            add(entityTable.CODE.eq(code))
            this
        }.getOrNull()
    }

    override suspend fun getSingleById(id: Long): PlayerRole? {
        return getSingleEntityAsync(PlayerRole::class.java) {
            add(entityTable.ID.eq(id))
            this
        }.getOrNull()
    }

    override suspend fun getAll(): ArrayList<PlayerRole> {
        return getAllEntitiesAsync(PlayerRole::class.java)
    }

    override suspend fun isExistByCode(code: String): Boolean {
        return isEntityExistAsync {
            it.add(entityTable.CODE.eq(code))
            it
        }
    }

    override suspend fun deleteByCode(code: String): Boolean {
        return deleteEntityAsync {
            add(entityTable.CODE.eq(code))
            this
        } > 0
    }
}