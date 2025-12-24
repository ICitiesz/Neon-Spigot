package com.islandstudio.neon.apinew.status

sealed class ActionResultStatus {
    abstract val message: String

    data class Success(override var message: String = "Action success"): ActionResultStatus()

    data class FailedToCreatePlayerProfile(override val message: String = "Failed to create player profile")
        : ActionResultStatus()

    data class FailedToCreatePlayerRole(override val message: String = "Failed to create player role")
        : ActionResultStatus()

    data class FailedToUpdatePlayerRole(override val message: String = "Failed to update player role")
        : ActionResultStatus()

    data class PlayerProfileNotExist(override val message: String = "Player profile does not exist")
        : ActionResultStatus()

    data class PlayerRoleAlreadyExist(override val message: String = "Player role already exist")
        : ActionResultStatus()

    data class PlayerRoleNotExist(override val message: String = "Player role does not exist")
        : ActionResultStatus()
}