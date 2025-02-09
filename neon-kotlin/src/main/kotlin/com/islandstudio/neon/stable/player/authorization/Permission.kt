package com.islandstudio.neon.stable.player.authorization

sealed class Permission: AbstractPermission() {
    data object RoleManagement: Permission() {
        override val permissionCode: String = "ROLE_MANAGEMENT"
        override val description: String = "Ability to manage role operation such as create/remove role as well as assign/unassign role to player."
    }
}