package com.islandstudio.neon.persistence

import com.islandstudio.neon.api.context.CoroutineUserContext
import kotlinx.coroutines.currentCoroutineContext
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class DatabaseContext(private val exposedDatabase: Database) {
//    fun initializeSchema() {
//        transaction(exposedDatabase) {
//            SchemaUtils.create(Users, Products)
//        }
//    }

    fun <T> execute(block: Transaction.() -> T): T {
        return transaction(exposedDatabase) {
            block(this)
        }
    }

    suspend fun <T> executeAsync(block: suspend Transaction.() -> T): T {
        return suspendTransaction(exposedDatabase) {
            currentCoroutineContext()[CoroutineUserContext.CoroutineContextKey] ?: throw Exception("Invalid coroutine context")

            block(this)
        }
    }
}