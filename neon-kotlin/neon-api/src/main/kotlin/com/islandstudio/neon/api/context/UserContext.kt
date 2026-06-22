package com.islandstudio.neon.api.context

data class UserContext(
    val actorType: ActorType,
    val name: String,
    val uuid: String?
) {
    companion object {
        fun createAsPlayer(name: String, uuid: String): UserContext {
            return UserContext(ActorType.PLAYER, name, uuid)
        }

        fun createAsSystem(): UserContext {
            return UserContext(ActorType.SYSTEM, "SYSTEM", null)
        }
    }
}