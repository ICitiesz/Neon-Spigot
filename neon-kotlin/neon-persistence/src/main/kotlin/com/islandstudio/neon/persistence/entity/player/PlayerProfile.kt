package com.islandstudio.neon.persistence.entity.player

import com.islandstudio.neon.persistence.entity.BaseEntity
import com.islandstudio.neon.persistence.entity.BaseEntity.AuditProperties
import com.islandstudio.neon.persistence.table.BaseTable
import com.islandstudio.neon.persistence.table.IdColumn
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder
import org.jetbrains.exposed.v1.javatime.datetime
import java.time.LocalDateTime

data class PlayerProfile(
    val id: Long = 0,
    val uuid: String,
    val name: String,
    val joinedAt: LocalDateTime,
    val roleId: Long? = null,
    override val auditProperties: AuditProperties = AuditProperties()
): BaseEntity()

object PlayerProfileTable: BaseTable<PlayerProfile>("player_profile"), IdColumn {
    override val id: Column<Long> = long("Id").autoIncrement()

    val uuid: Column<String> = varchar("UUID", 36)
    val name: Column<String> = varchar("Name", 16)
    val joinedAt: Column<LocalDateTime> = datetime("JoinedAt")
    val roleId: Column<Long?> = long("RoleId").nullable()

    override val primaryKey = PrimaryKey(id)

    override fun toTable(statement: UpdateBuilder<*>, entity: PlayerProfile) {
        statement[uuid] = entity.uuid
        statement[name] = entity.name
        statement[joinedAt] = entity.joinedAt
        statement[roleId] = entity.roleId
    }

    override fun toEntity(resultRow: ResultRow?): PlayerProfile? {
        return resultRow?.let {
            PlayerProfile(
                id = it[this.id],
                uuid = it[this.uuid],
                name = it[this.name],
                joinedAt = it[this.joinedAt],
                roleId = it[this.roleId],
                auditProperties = AuditProperties(
                    createdAt = it[this@PlayerProfileTable.createdAt],
                    createdBy = it[this@PlayerProfileTable.createdBy],
                    modifiedAt = it[this@PlayerProfileTable.modifiedAt],
                    modifiedBy = it[this@PlayerProfileTable.modifiedBy]
                )
            )
        }
    }
}