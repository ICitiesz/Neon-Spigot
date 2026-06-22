package com.islandstudio.neon.api.result

import com.islandstudio.neon.api.dto.player.PlayerProfileDto

sealed class CreatePlayerProfileResult {
    data class Success(val playerProfileDto: PlayerProfileDto) : CreatePlayerProfileResult()
    data class PlayerProfileAlreadyExists(val uuid: String) : CreatePlayerProfileResult()
    data class Error(val message: String) : CreatePlayerProfileResult()
}

sealed class UpdatePlayerProfileResult {
    data class Success(val playerProfileDto: PlayerProfileDto) : UpdatePlayerProfileResult()
    data class PlayerProfileNotFound(val uuid: String) : UpdatePlayerProfileResult()
    data class Error(val message: String) : UpdatePlayerProfileResult()
}
