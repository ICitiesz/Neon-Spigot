package com.islandstudio.neon.apinew.entity.player

import com.islandstudio.neon.apinew.entity.BaseEntity
import com.islandstudio.neon.apinew.schema.tables.records.PermissionRecord

data class Permission(
    val id: Long? = null,
    val parentId: Long? = null,
    val name: String,
    val code: String,
    val description: String
): BaseEntity<Permission, PermissionRecord>(PermissionRecord::class.java)
