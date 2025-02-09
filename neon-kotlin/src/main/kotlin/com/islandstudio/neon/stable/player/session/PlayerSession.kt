package com.islandstudio.neon.stable.player.session

import java.io.Serializable
import java.util.*

data class PlayerSession(
    val playerUUID: UUID,
    val playerName: String,
    val roleId: Long?
): Serializable
