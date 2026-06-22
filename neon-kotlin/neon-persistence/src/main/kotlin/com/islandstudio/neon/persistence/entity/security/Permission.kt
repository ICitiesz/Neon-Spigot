package com.islandstudio.neon.persistence.entity.security

import com.islandstudio.neon.persistence.entity.BaseEntity
import com.islandstudio.neon.persistence.table.BaseTable
import com.islandstudio.neon.persistence.table.IdColumn
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder

data class Permission(
    val id: Long = 0,
    val parentId: Long? = null,
    val name: String,
    val code: String,
    val description: String,
    override val auditProperties: AuditProperties = AuditProperties(),
): BaseEntity()

object PermissionTable: BaseTable<Permission>("permission"), IdColumn {
    override val id: Column<Long> = long("Id").autoIncrement()
    val parentId: Column<Long?> = long("ParentId").nullable()
    val name: Column<String> = varchar("Name", 64)
    val code: Column<String> = varchar("Code", 64)
    val description: Column<String> = varchar("Description", 256)

    override val primaryKey = PrimaryKey(id)

    override fun toTable(statement: UpdateBuilder<*>, entity: Permission) {
        statement[parentId] = entity.parentId
        statement[name] = entity.name
        statement[code] = entity.code
        statement[description] = entity.description
    }

    override fun toEntity(resultRow: ResultRow?): Permission? {
        return resultRow?.let {
            Permission(
                id = it[this.id],
                parentId = it[this.parentId],
                name = it[this.name],
                code = it[this.code],
                description = it[this.description],
                auditProperties = BaseEntity.AuditProperties(
                    createdAt = it[this@PermissionTable.createdAt],
                    createdBy = it[this@PermissionTable.createdBy],
                    modifiedAt = it[this@PermissionTable.modifiedAt],
                )
            )
        }
    }
}