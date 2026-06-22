package com.islandstudio.neon.persistence.entity

import java.time.LocalDateTime

abstract class BaseEntity {
    abstract val auditProperties: AuditProperties

    data class AuditProperties(
        val createdAt: LocalDateTime = LocalDateTime.now(),
        val createdBy: String = "SYSTEM",
        val modifiedAt: LocalDateTime = LocalDateTime.now(),
        val modifiedBy: String = "SYSTEM"
    )
}