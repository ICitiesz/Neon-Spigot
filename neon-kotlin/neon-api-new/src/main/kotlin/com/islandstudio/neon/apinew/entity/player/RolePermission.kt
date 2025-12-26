package com.islandstudio.neon.apinew.entity.player

import com.islandstudio.neon.apinew.entity.BaseEntity
import com.islandstudio.neon.apinew.schema.tables.records.RolePermissionRecord

data class RolePermission(
    val id: Long? = null,
    val parentId: Long? = null,
    val roleId: Long,
    val permissionId: Long
): BaseEntity<RolePermission, RolePermissionRecord>(RolePermissionRecord::class.java)
