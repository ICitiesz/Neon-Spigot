package com.islandstudio.neon.stable.player.nAccessPermission

enum class Permission(
    val permissionName: String,
    val permissionCode: String,
    val permissionDesc: String
) {
    AP_ACCESS_NSERVER_FEATURE(
        "Access nServerFeature",
        "ACCESS_NSERVER_FEATURE",
        "Grant access to using nServerFeature."
    ),

    AP_ACCESS_NROLE(
        "Access nRole",
        "ACCESS_NROLE",
        "Grant access to using nRole."
    ),

    AP_ACCESS_NACCESS_PERMISSION(
        "Access nAccessPermission",
        "ACCESS_NACCESS_PERMISSION",
        "Grant access to using nAccessPermission."
    ),

    AP_ACCESS_NDURABLE_CONFIG(
        "Access nDurable Configu",
        "ACCESS_NDURABLE_CONFIG",
        "Grant access to modify nDurable configuration."
    );

    companion object {
        fun valueOfPermissionCode(permissionCode: String): Permission? {
            return Permission.entries.find { it.permissionCode == permissionCode }
        }
    }

    enum class AccessType {
        FULL,
        LIMITED;

        companion object {
            fun valueOfAccessType(accessType: String): AccessType? {
                return AccessType.entries.find { accessType.uppercase() == it.toString() }
            }

            fun hasAccessType(accessType: String): Boolean {
                AccessType.entries.find {
                    accessType.uppercase() == it.toString()
                }?.let {
                    return true
                }

                return false
            }
        }
    }
}