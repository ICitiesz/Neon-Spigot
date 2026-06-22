package com.islandstudio.neon.persistence.repository

import com.islandstudio.neon.persistence.DatabaseContext
import com.islandstudio.neon.persistence.entity.BaseEntity
import com.islandstudio.neon.persistence.table.BaseTable
import com.islandstudio.neon.persistence.table.IdColumn
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.statements.UpdateStatement
import org.jetbrains.exposed.v1.jdbc.*

abstract class BaseRepositoryImpl<T: BaseEntity, V>(
    protected val databaseContext: DatabaseContext,
    protected val table: V
): BaseRepository<T> where V: BaseTable<T>, V: IdColumn {
    override suspend fun insertEntityAsync(entity: T): Int = databaseContext.executeAsync {
        table.insert {
            this.toTable(it, entity)
            this.updateAuditColumn(it)
        }.insertedCount
    }

    override suspend fun insertEntityReturnAsync(entity: T): T? = databaseContext.executeAsync {
        val result = table.insertReturning {
            this.toTable(it, entity)
            this.updateAuditColumn(it)
        }.singleOrNull()

        table.toEntity(result)
    }


    override suspend fun updateEntityAsync(wherePredicate: () -> Op<Boolean>, updateStatement: (UpdateStatement) -> Unit): Int = databaseContext.executeAsync {
        table.update(where = wherePredicate) {
            updateStatement(it)
            this.updateAuditColumn(it)
        }
    }

    override suspend fun updateEntityReturnAsync(id: Long, wherePredicate: () -> Op<Boolean>, updateStatement: (UpdateStatement) -> Unit): T? = databaseContext.executeAsync {
        /* Should make it update & return manually as UPDATE ... RETURN statement only available on MariaDB 13.0++ */
        table.update(where = wherePredicate) {
            updateStatement(it)
            this.updateAuditColumn(it)
        }

        val result = table.selectAll()
            .where(table.id.eq(id))
            .singleOrNull()

        table.toEntity(result)
    }

    override suspend fun getSingleEntityAsync(wherePredicate: () -> Op<Boolean>): T? = databaseContext.executeAsync {
        val result = table.selectAll().where {
            wherePredicate()
        }.singleOrNull()

        table.toEntity(result)
    }


    override suspend fun getListEntityAsync(wherePredicate: () -> Op<Boolean>): MutableList<T> = databaseContext.executeAsync {
        val result = table.selectAll().where {
            wherePredicate()
        }.mapNotNull {
            table.toEntity(it)
        }.toMutableList()

        result
    }

    override suspend fun deleteEntityAsync(wherePredicate: () -> Op<Boolean>): Int = databaseContext.executeAsync {
        table.deleteWhere { wherePredicate() }
    }
}

private interface BaseRepository<T: BaseEntity> {
    suspend fun insertEntityAsync(entity: T): Int
    suspend fun insertEntityReturnAsync(entity: T): T?

    suspend fun updateEntityAsync(wherePredicate: () -> Op<Boolean>, updateStatement: (UpdateStatement) -> Unit): Int
    suspend fun updateEntityReturnAsync(id: Long, wherePredicate: () -> Op<Boolean> = { Op.TRUE }, updateStatement: (UpdateStatement) -> Unit): T?

    suspend fun getSingleEntityAsync(wherePredicate: () -> Op<Boolean>): T?
    suspend fun getListEntityAsync(wherePredicate: () -> Op<Boolean>): MutableList<T>

    suspend fun deleteEntityAsync(wherePredicate: () -> Op<Boolean>): Int
}