package com.islandstudio.neon.api.entity

import java.io.Serializable
import java.time.LocalDateTime
import java.time.ZoneOffset

interface IAuditBase<T>: Serializable {
    companion object {
        fun defaultTime(): LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)
        fun defaultAuditor(): String = "SYSTEM"
    }
    var createdAt: LocalDateTime
    var createdBy: String
    var modifiedAt: LocalDateTime
    var modifiedBy: String

    fun updateCreated(name: String): T {
        createdAt = LocalDateTime.now(ZoneOffset.UTC)
        createdBy = name

        return this as T
    }

    fun updateModified(name: String): T {
        modifiedAt = LocalDateTime.now(ZoneOffset.UTC)
        modifiedBy = name

        return this as T
    }

    fun updateCreatedModified(name: String): T {
        updateCreated(name)
        updateModified(name)

        return this as T
    }
}