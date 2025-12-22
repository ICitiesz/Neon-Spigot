package com.islandstudio.neon.apinew.entity

import com.islandstudio.neon.shared.core.di.IComponentProvider
import com.islandstudio.neon.shared.core.di.getComponent
import com.islandstudio.neon.shared.utils.data.DataUtil
import com.islandstudio.neon.shared.utils.data.ModelMapperUtil
import org.jooq.Record
import java.io.Serializable
import java.time.LocalDateTime
import java.time.ZoneOffset

abstract class BaseEntity<TEntity, TEntityRecord: Record>(private val entityRecordClazz: Class<TEntityRecord>): Serializable, IComponentProvider {
    companion object {
        val defaultTime: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)
        val defaultAuditor: String = "SYSTEM"
    }

    private val mapper = getComponent<ModelMapperUtil>()

    var createdAt: LocalDateTime = defaultTime
    var createdBy: String = defaultAuditor
    var modifiedAt: LocalDateTime = defaultTime
    var modifiedBy: String = defaultAuditor

    fun updateCreated(name: String?): TEntity {
        createdAt = LocalDateTime.now(ZoneOffset.UTC)
        createdBy = name ?: defaultAuditor

        return this as TEntity
    }

    fun updateModified(name: String?): TEntity {
        modifiedAt = LocalDateTime.now(ZoneOffset.UTC)
        modifiedBy = name ?: defaultAuditor

        return this as TEntity
    }

    fun updateCreatedModified(name: String?): TEntity {
        updateCreated(name)
        updateModified(name)

        return this as TEntity
    }

    fun toRecord(): TEntityRecord {
        return mapper.mapTo(this, entityRecordClazz)
    }

    fun getEntityClass(): Class<TEntity> {
        return DataUtil.asType(this::class.java )
    }
}