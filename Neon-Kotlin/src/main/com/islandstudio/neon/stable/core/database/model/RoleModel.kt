package com.islandstudio.neon.stable.core.database.model


data class RoleModel (
    var roleId: Long? = null,
    var roleDisplayName: String? = null,
    var roleCode: String? = null,
    var assignedPlayerCount: Long? = null
)