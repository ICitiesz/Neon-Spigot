package com.islandstudio.neon.stable.core.database.model

import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.pojos.RoleAccess
import com.islandstudio.neon.stable.player.nAccessPermission.Permission

data class RoleWithPermissionModel(
    val roleId: Long? = null,
    val roleDisplayName: String? = null,
    val roleCode: String? = null,
    val assignedPlayerCount: Long = 0,
    val grantedPermissions: ArrayList<RoleAccess> = ArrayList(),
    val revokedPermissions: ArrayList<RoleAccess> = ArrayList()
) {
    fun clone(): RoleWithPermissionModel {
        @Suppress("UNCHECKED_CAST")
        return RoleWithPermissionModel(
            this.roleId,
            this.roleDisplayName,
            this.roleCode,
            this.assignedPlayerCount,
            this.grantedPermissions.clone() as ArrayList<RoleAccess>,
            this.revokedPermissions.clone() as ArrayList<RoleAccess>
        )
    }

    fun updatePermissionToGranted(permissionId: Long, accessType: Permission.AccessType) {
        revokedPermissions.find {
            it.permissionId == permissionId
        }?.let {
            grantedPermissions.add(it)
            revokedPermissions.remove(it)
            return
        }

        grantedPermissions.add(RoleAccess(
            roleId = roleId,
            permissionId = permissionId,
            accessType = accessType.toString()
        ))
    }

    fun updatePermissionToRevoked(permissionId: Long) {
        grantedPermissions.find {
            it.permissionId == permissionId
        }?.let {
            if (it.roleAccessId == null) {
                grantedPermissions.remove(it)
                return
            }

            revokedPermissions.add(it)
            grantedPermissions.remove(it)
        }
    }

    fun updateAccessType(permissionId: Long, accessType: Permission.AccessType) {
        grantedPermissions.find {
            it.permissionId == permissionId
        }?.let {
            it.accessType = accessType.toString()
        }

        revokedPermissions.find {
            it.permissionId == permissionId
        }?.let {
            it.accessType = accessType.toString()
        }
    }
}
