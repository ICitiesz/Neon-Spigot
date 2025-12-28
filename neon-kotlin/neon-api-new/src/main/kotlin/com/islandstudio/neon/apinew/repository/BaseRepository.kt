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

    override suspend fun insertEntityListAsync(entityList: ArrayList<TEntity>): Int {
        return withContext(Dispatchers.IO) {
            databaseContext.insertInto(entityTable)
                .set(entityList.map { it.toRecord() })
                .executeAsync()
                .await()
        }
    }

    override suspend fun insertEntityListReturnAsync(entityClazz: Class<TEntity>, entityList: ArrayList<TEntity>): ArrayList<TEntity> {
        return withContext(Dispatchers.IO) {
            databaseContext.insertInto(entityTable)
                .set(entityList.map { it.toRecord() })
                .returning()
                .fetchInto(entityClazz)
                .toCollection(ArrayList())
        }
    }

    override suspend fun getSingleEntityAsync(entityClazz: Class<TEntity>, whereStep: ArrayList<Condition>.() -> ArrayList<Condition>): Optional<TEntity> {
        return withContext(Dispatchers.IO) {
            databaseContext
                .selectFrom(entityTable)
                .where(whereStep(arrayListOf()))
                .fetchOneInto(entityClazz)
                ?.let { Optional.of(it) }
                ?: Optional.empty()
        }
    }

    override suspend fun getListEntityAsync(entityClazz: Class<TEntity>, whereStep: ArrayList<Condition>.() -> ArrayList<Condition>): ArrayList<TEntity> {
        return withContext(Dispatchers.IO) {
            databaseContext
                .selectFrom(entityTable)
                .where(whereStep(arrayListOf()))
                .fetch()
                .into(entityClazz)
                .toCollection(ArrayList())
        }
    }

    override suspend fun getAllEntitiesAsync(entityClazz: Class<TEntity>): ArrayList<TEntity> {
        return withContext(Dispatchers.IO) {
            databaseContext
                .selectFrom(entityTable)
                .fetch()
                .into(entityClazz)
                .toCollection(ArrayList())
        }
    }

    override suspend fun updateEntityAsync(entity: TEntity, whereSteps: ArrayList<Condition>.() -> ArrayList<Condition>): Int {
        return withContext(Dispatchers.IO) {
            databaseContext
                .update(entityTable)
                .set(entity.toRecord())
                .where(whereSteps(arrayListOf()))
                .execute()
        }
    }

    override suspend fun updateEntityReturnAsync(entity: TEntity, whereSteps: ArrayList<Condition>.() -> ArrayList<Condition>): Optional<TEntity> {
        return withContext(Dispatchers.IO) {
            databaseContext.update(entityTable)
                .set(entity.toRecord())
                .where(whereSteps(arrayListOf()))
                .returning()
                .fetchOneInto(entity.getEntityClass())
                ?.let { Optional.of(it) }
                ?: Optional.empty()
        }
    }

    override suspend fun deleteEntityAsync(whereSteps: ArrayList<Condition>.() -> ArrayList<Condition>): Int {
        return withContext(Dispatchers.IO) {
            databaseContext
                .deleteFrom(entityTable)
                .where(whereSteps(arrayListOf()))
                .executeAsync()
                .await()
        }
    }

    override suspend fun isEntityExistAsync(whereStep: (ArrayList<Condition>) -> ArrayList<Condition>): Boolean {
        return withContext(Dispatchers.IO) {
            databaseContext.fetchExists(
                entityTable,
                whereStep(arrayListOf())
            )
        }
    }
}

private interface IBaseRepository<TEntity: BaseEntity<TEntity, out Record>, TEntityTable: TableImpl<out Record>> {
    suspend fun insertEntityAsync(entity: TEntity): Int
    suspend fun insertEntityReturnAsync(entity: TEntity): Optional<TEntity>
    suspend fun insertEntityListAsync(entityList: ArrayList<TEntity>): Int
    suspend fun insertEntityListReturnAsync(
        entityClazz: Class<TEntity>,
        entityList: ArrayList<TEntity>
    ): ArrayList<TEntity>

    suspend fun getSingleEntityAsync(entityClazz: Class<TEntity>, whereStep: ArrayList<Condition>.() -> ArrayList<Condition>): Optional<TEntity>
    suspend fun getListEntityAsync(entityClazz: Class<TEntity>, whereStep: ArrayList<Condition>.() -> ArrayList<Condition>): ArrayList<TEntity>
    suspend fun getAllEntitiesAsync(entityClazz: Class<TEntity>): ArrayList<TEntity>

    suspend fun updateEntityAsync(entity: TEntity, whereSteps: ArrayList<Condition>.() -> ArrayList<Condition>): Int
    suspend fun updateEntityReturnAsync(entity: TEntity, whereSteps: ArrayList<Condition>.() -> ArrayList<Condition>): Optional<TEntity>

    suspend fun deleteEntityAsync(whereSteps: ArrayList<Condition>.() -> ArrayList<Condition>): Int

    suspend fun isEntityExistAsync(whereStep: (ArrayList<Condition>) -> ArrayList<Condition>): Boolean
}