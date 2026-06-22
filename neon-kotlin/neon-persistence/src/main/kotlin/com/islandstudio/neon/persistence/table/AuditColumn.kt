package com.islandstudio.neon.persistence.table

import org.jetbrains.exposed.v1.core.Column
import java.time.LocalDateTime

interface AuditColumn {
    val createdAt: Column<LocalDateTime>
    val createdBy: Column<String>
    val modifiedAt: Column<LocalDateTime>
    val modifiedBy: Column<String>
}