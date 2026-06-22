package com.islandstudio.neon.persistence.entity.security

import com.islandstudio.neon.persistence.entity.BaseEntity
import com.islandstudio.neon.persistence.entity.BaseEntity.AuditProperties
import com.islandstudio.neon.persistence.table.BaseTable
import com.islandstudio.neon.persistence.table.IdColumn
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder

data class RolePermission(
    val id: Long = 0,
    val parentId: Long? = null,
    val roleId: Long,
    val permissionId: Long,
    override val auditProperties: AuditProperties = AuditProperties()
): BaseEntity()

object RolePermissionTable: BaseTable<RolePermission>("role_permission"), IdColumn {
    override val id: Column<Long> = long("Id").autoIncrement()
    val parentId: Column<Long?> = long("ParentId").nullable()
    val roleId: Column<Long> = long("RoleId")
    val permissionId: Column<Long> = long("PermissionId")

    override fun toTable(statement: UpdateBuilder<*>, entity: RolePermission, ) {
        statement[parentId] = entity.parentId
        statement[roleId] = entity.roleId
        statement[permissionId] = entity.permissionId
    }

    override fun toEntity(resultRow: ResultRow?): RolePermission? {
        return resultRow?.let {
            RolePermission(
                id = it[this.id],
                parentId = it[this.parentId],
                roleId = it[this.roleId],
                permissionId = it[this.permissionId],
                auditProperties = AuditProperties(
                    createdAt = it[this@RolePermissionTable.createdAt],
                    createdBy = it[this@RolePermissionTable.createdBy],
                    modifiedAt = it[this@RolePermissionTable.modifiedAt],
                    modifiedBy = it[this@RolePermissionTable.modifiedBy]
                )
            )
        }
    }
}