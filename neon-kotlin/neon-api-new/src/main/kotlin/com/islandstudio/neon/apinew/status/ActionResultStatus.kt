package com.islandstudio.neon.apinew.status

sealed class ActionResultStatus {
    abstract val message: String

    data class Success(override var message: String = "Action success"): ActionResultStatus()

    data class FailedToCreatePlayerProfile(override val message: String = "Failed to create player profile")
        : ActionResultStatus()

    data class PlayerProfileNotExist(override val message: String = "Player profile does not exist")
        : ActionResultStatus()
}