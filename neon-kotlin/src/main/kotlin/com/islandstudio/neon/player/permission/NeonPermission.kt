package com.islandstudio.neon.player.permission

sealed class NeonPermission(mainPermission: AbstractPermission? = null): AbstractPermission(mainPermission) {
    companion object {
        fun getAllPermission(): ArrayList<NeonPermission> {
            return NeonPermission::class.sealedSubclasses
                .map { it.objectInstance as NeonPermission }
                .toCollection(ArrayList())
        }

        fun getAllSubPermission(): ArrayList<NeonPermission> {
            return getAllPermission().filter { it.mainPermission != null }.toCollection(ArrayList())
        }

        fun getAllMainPermission(): ArrayList<NeonPermission> {
            return getAllPermission().filter { it.mainPermission == null }.toCollection(ArrayList())
        }

        fun getSubPermissions(mainNeonPermission: NeonPermission): ArrayList<NeonPermission> {
            return getAllPermission().filter { it.mainPermission == mainNeonPermission }.toCollection(ArrayList())
        }
    }

    data object RoleManagement: NeonPermission() {
        override val permissionCode: String = "ROLE_MANAGEMENT"
        override val description: String = "Ability to manage role operation such as create/remove role as well as assign/unassign role to player."
    }

    data object NeonPermissionManagement: NeonPermission() {
        override val permissionCode: String = "PERMISSION_MANAGEMENT"
        override val description: String = "Ability to manage permission operation such as grant/revoke permission to the role."
    }

    data object NeonFeatureManagement: NeonPermission() {
        override val permissionCode: String = "NEON_FEATURE_MANAGEMENT"
        override val description: String = "Ability to manage neon feature operation such as toggle on/off neon features, " +
                "and set option of the neon feature."
    }
}