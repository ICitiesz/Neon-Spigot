package com.islandstudio.neon.persistence.table

import com.islandstudio.neon.apirework.context.SecurityContextHolder
import com.islandstudio.neon.persistence.entity.BaseEntity
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.statements.InsertStatement
import org.jetbrains.exposed.v1.core.statements.StatementType
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder
import org.jetbrains.exposed.v1.javatime.datetime
import java.time.LocalDateTime

abstract class BaseTable<T: BaseEntity>(name: String): Table(name), AuditColumn {
    override val createdAt: Column<LocalDateTime> = datetime("CreatedAt")
    override val createdBy: Column<String> = varchar("CreatedBy", 16)
    override val modifiedAt: Column<LocalDateTime> = datetime("ModifiedAt")
    override val modifiedBy: Column<String> = varchar("ModifiedBy", 16)

    abstract fun toTable(statement: UpdateBuilder<*>, entity: T)
    abstract fun toEntity(resultRow: ResultRow?): T?

    fun updateAuditColumn(statement: UpdateBuilder<*>) {
        val currentDate = LocalDateTime.now()
        val currentUserContext = SecurityContextHolder.getContext() ?: throw IllegalStateException("Invalid user context")

        when (statement.type) {
            StatementType.INSERT if statement is InsertStatement<*> -> {
                statement[createdAt] = currentDate
                statement[createdBy] = currentUserContext.name
                statement[modifiedAt] = currentDate
                statement[modifiedBy] = currentUserContext.name
            }
            StatementType.UPDATE -> {
                statement[modifiedAt] = currentDate
                statement[modifiedBy] = currentUserContext.name
            }

            else -> {}
        }
    }
}