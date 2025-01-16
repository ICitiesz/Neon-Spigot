package com.islandstudio.neon.stable.primary.nProfile

import org.bukkit.entity.Player
import org.json.simple.JSONObject

data class PlayerProfile(val player: Player) {
    private val playerProfile: JSONObject = NProfile.Handler.getProfileData(player)

    val playerUUID: String = playerProfile["UUID"] as String
    val playerName: String = playerProfile["Name"] as String
    val playerRank: String = (playerProfile["Rank"] as String).lowercase()
    val playerMuteStatus: Boolean = playerProfile["isMuted"] as Boolean
}
