package com.islandstudio.neon.persistence.entity.player

import com.islandstudio.neon.persistence.entity.BaseEntity
import com.islandstudio.neon.persistence.table.BaseTable
import com.islandstudio.neon.persistence.table.IdColumn
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder

data class PlayerRole(
    val id: Long = 0,
    val displayName: String,
    val code: String,
    override val auditProperties: AuditProperties = AuditProperties()
): BaseEntity()

object PlayerRoleTable: BaseTable<PlayerRole>("player_role"), IdColumn {
    override val id: Column<Long> = long("Id").autoIncrement()
    val displayName: Column<String> = varchar("DisplayName", 64)
    val code: Column<String> = varchar("Code", 64)

    override val primaryKey = PrimaryKey(id)

    override fun toTable(statement: UpdateBuilder<*>, entity: PlayerRole) {
        statement[displayName] = entity.displayName
        statement[code] = entity.code
    }

    override fun toEntity(resultRow: ResultRow?): PlayerRole? {
        return resultRow?.let {
            PlayerRole(
                id = it[this.id],
                displayName = it[this.displayName],
                code = it[this.code],
                auditProperties = BaseEntity.AuditProperties(
                    createdAt = it[this@PlayerRoleTable.createdAt],
                    createdBy = it[this@PlayerRoleTable.createdBy],
                    modifiedAt = it[this@PlayerRoleTable.modifiedAt],
                )
            )
        }
    }
}