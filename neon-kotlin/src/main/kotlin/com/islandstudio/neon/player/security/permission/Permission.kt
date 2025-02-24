package com.islandstudio.neon.player.security.permission

sealed class Permission(mainPermission: AbstractPermission? = null): AbstractPermission(mainPermission) {
    companion object {
        fun getAllPermission(): ArrayList<Permission> {
            return Permission::class.sealedSubclasses
                .map { it.objectInstance as Permission }
                .toCollection(ArrayList())
        }

        fun getAllSubPermission(): ArrayList<Permission> {
            return getAllPermission().filter { it.mainPermission != null }.toCollection(ArrayList())
        }

        fun getAllMainPermission(): ArrayList<Permission> {
            return getAllPermission().filter { it.mainPermission == null }.toCollection(ArrayList())
        }

        fun getSubPermissions(mainPermission: Permission): ArrayList<Permission> {
            return getAllPermission().filter { it.mainPermission == mainPermission }.toCollection(ArrayList())
        }
    }

    data object RoleManagement: Permission() {
        override val permissionCode: String = "ROLE_MANAGEMENT"
        override val description: String = "Ability to manage role operation such as create/remove role as well as assign/unassign role to player."
    }

    data object PermissionManagement: Permission() {
        override val permissionCode: String = "PERMISSION_MANAGEMENT"
        override val description: String = "Ability to manage permission operation such as grant/revoke permission to the role."
    }
}