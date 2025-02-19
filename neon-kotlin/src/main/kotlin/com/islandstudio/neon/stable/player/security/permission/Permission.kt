package com.islandstudio.neon.stable.player.security.permission

sealed class Permission: AbstractPermission() {
    data object RoleManagement: Permission() {
        override val permissionCode: String = "ROLE_MANAGEMENT"
        override val description: String = "Ability to manage role operation such as create/remove role as well as assign/unassign role to player."
    }

    data object PermissionManagement: Permission() {
        override val permissionCode: String = "PERMISSION_MANAGEMENT"
        override val description: String = "Ability to manage permission operation such as grant/revoke permission to the role."
    }
}