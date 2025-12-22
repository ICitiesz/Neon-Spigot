package com.islandstudio.neon.apinew.entity.player

import com.islandstudio.neon.apinew.entity.BaseEntity
import com.islandstudio.neon.apinew.schema.tables.records.PlayerRoleRecord

data class PlayerRole(
    var id: Long? = null,
    var displayName: String,
    var code: String,
): BaseEntity<PlayerRole, PlayerRoleRecord>(PlayerRoleRecord::class.java)