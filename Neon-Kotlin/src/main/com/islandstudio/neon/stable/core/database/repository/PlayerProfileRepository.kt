package com.islandstudio.neon.stable.core.database.repository

import com.islandstudio.neon.stable.core.application.di.ModuleInjector
import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.pojos.PlayerProfile
import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.records.DtPlayerProfileRecord
import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.references.DT_PLAYER_PROFILE
import org.jooq.DSLContext
import org.koin.core.annotation.Singleton
import org.koin.core.component.inject
import java.util.*

@Singleton
class PlayerProfileRepository: ModuleInjector {
    private val dbContext by inject<DSLContext>()

    fun addPlayerProfile(playerProfile: PlayerProfile) {
        with(DtPlayerProfileRecord(playerProfile)) {
            dbContext
                .insertInto(DT_PLAYER_PROFILE)
                .set(this)
                .execute()
        }
    }

    fun getByPlayerUUID(playerUUID: UUID): PlayerProfile? {
        return dbContext
            .select()
            .from(DT_PLAYER_PROFILE)
            .where(DT_PLAYER_PROFILE.PLAYER_UUID.eq(playerUUID))
            .fetchOneInto(PlayerProfile::class.java)
    }

    fun getByPlayerName(playerName: String): PlayerProfile? {
        return dbContext
            .select()
            .from(DT_PLAYER_PROFILE)
            .where(DT_PLAYER_PROFILE.PLAYER_NAME.eq(playerName))
            .fetchOneInto(PlayerProfile::class.java)
    }

    fun getAll(): List<PlayerProfile> {
        return dbContext.fetch(DT_PLAYER_PROFILE).into(PlayerProfile::class.java)
    }

    fun hasRoleAssign(playerUUID: UUID): Boolean {
        val sqlQuery = dbContext
            .select()
            .from(DT_PLAYER_PROFILE)
            .where(DT_PLAYER_PROFILE.PLAYER_UUID.eq(playerUUID)
                .and(DT_PLAYER_PROFILE.ROLE_ID.isNotNull)
            )

        return dbContext.fetchExists(sqlQuery)
    }

    fun getAssignedRoleId(playerUUID: UUID): Long? {
        return dbContext
            .select(DT_PLAYER_PROFILE.ROLE_ID)
            .from(DT_PLAYER_PROFILE)
            .where(DT_PLAYER_PROFILE.PLAYER_UUID.eq(playerUUID))
            .fetchOne(DT_PLAYER_PROFILE.ROLE_ID)
    }

    fun updatePlayerProfile(playerProfile: PlayerProfile) {
       with(DtPlayerProfileRecord(playerProfile)) {
           dbContext
               .update(DT_PLAYER_PROFILE)
               .set(this)
               .where(DT_PLAYER_PROFILE.PLAYER_UUID.eq(this.playerUuid))
               .execute()
       }
    }
}