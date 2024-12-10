package com.islandstudio.neon.stable.core.database

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.islandstudio.neon.stable.core.application.di.ModuleInjector
import org.jooq.Record
import org.jooq.Result
import org.jooq.ResultQuery
import java.util.concurrent.TimeUnit

object DatabaseCacheManager: ModuleInjector, IDatabaseContext {
    private lateinit var databaseCache: Cache<ResultQuery<*>, Result<out Record>>
    //private val dslContext by inject<DSLContext>()
    private val dslContext = getDatabaseContext()

    object Handler {
        fun run() {
            databaseCache = CacheBuilder.newBuilder()
                .maximumSize(10000)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .expireAfterWrite(20, TimeUnit.MINUTES)
                .refreshAfterWrite(1, TimeUnit.SECONDS)
                .build(object : CacheLoader<ResultQuery<*>, Result<out Record>>() {
                    override fun load(key: ResultQuery<*>): Result<out Record> {
                        return dslContext.fetch(key)
                    }
                })
        }
    }

    fun getResultFromCache(sqlQuery: ResultQuery<*>): Result<out Record>? {
        return databaseCache.getIfPresent(sqlQuery)
    }

    fun addResultToCache(sqlQuery: ResultQuery<*>, result: Result<out Record>) {
        databaseCache.put(sqlQuery, result)
    }

    fun updateResultInCache(sqlQuery: ResultQuery<*>, result: Result<out Record>) {
        databaseCache.put(sqlQuery, result)
    }
}