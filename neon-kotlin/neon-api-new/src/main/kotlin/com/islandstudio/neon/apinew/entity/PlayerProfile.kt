package com.islandstudio.neon.apinew.entity

import com.islandstudio.neon.apinew.schema.tables.records.PlayerProfileRecord
import java.time.LocalDateTime
import java.util.*

data class PlayerProfile(
    var id: Long? = null,
    var uuid: UUID,
    var name: String,
    var roleId: Long? = null,
    var joinedAt: LocalDateTime,
): BaseEntity<PlayerProfile, PlayerProfileRecord>(PlayerProfileRecord::class.java)