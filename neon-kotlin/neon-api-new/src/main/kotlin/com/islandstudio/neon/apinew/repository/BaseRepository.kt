package com.islandstudio.neon.apinew.repository

import com.islandstudio.neon.apinew.connection.NeonDatabaseContext
import com.islandstudio.neon.apinew.entity.BaseEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import org.jooq.Condition
import org.jooq.Record
import org.jooq.impl.TableImpl
import java.util.*

abstract class BaseRepository<TEntity: BaseEntity<TEntity, out Record>, TEntityTable: TableImpl<out Record>>(
    protected val databaseContext: NeonDatabaseContext,
    protected val entityTable: TEntityTable,
): IBaseRepository<TEntity, TEntityTable> {

    override suspend fun insertEntityAsync(entity: TEntity): Int {
        return withContext(Dispatchers.IO) {
            databaseContext
                .insertInto(entityTable)
                .set(entity.toRecord())
                .executeAsync()
                .await()
        }
    }

    override suspend fun insertEntityReturnAsync(entity: TEntity): Optional<TEntity> {
        return withContext(Dispatchers.IO) {
            databaseContext
                .insertInto(entityTable)
                .set(entity.toRecord())
                .returning()
                .fetchOneInto(entity.getEntityClass())
                ?.let { Optional.of(it) }
                ?: Optional.empty()
        }
    }

    override suspend fun getSingleEntityAsync(entityClazz: Class<TEntity>, whereStep: (ArrayList<Condition>) -> ArrayList<Condition>): Optional<TEntity> {
        return withContext(Dispatchers.IO) {
            databaseContext
                .selectFrom(entityTable)
                .where(whereStep(arrayListOf()))
                .fetchOneInto(entityClazz)
                ?.let { Optional.of(it) }
                ?: Optional.empty()
        }
    }

    override suspend fun updateEntityAsync(entity: TEntity): Int {
        return databaseContext
            .update(entityTable)
            .set(entity.toRecord())
            .executeAsync()
            .await()
    }

    override suspend fun updateEntityReturnAsync(entity: TEntity): Optional<TEntity> {
        TODO("Not yet implemented")
    }
}

private interface IBaseRepository<TEntity: BaseEntity<TEntity, *>, TEntityTable: TableImpl<out Record>> {
    suspend fun insertEntityAsync(entity: TEntity): Int
    suspend fun insertEntityReturnAsync(entity: TEntity): Optional<TEntity>
    suspend fun updateEntityAsync(entity: TEntity): Int
    suspend fun updateEntityReturnAsync(entity: TEntity): Optional<TEntity>
    suspend fun getSingleEntityAsync(entityClazz: Class<TEntity>, whereStep: (ArrayList<Condition>) -> ArrayList<Condition>): Optional<TEntity>
    //suspend fun deleteEntityAsync(entity: TEntity): Int
    //suspend fun getSingleEntityAsync(id: Long): Optional<TEntity>
}