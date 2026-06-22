package com.islandstudio.neon.persistence.table

import com.islandstudio.neon.apirework.context.SecurityContextHolder
import org.jetbrains.exposed.v1.core.InternalApi
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.statements.GlobalStatementInterceptor
import org.jetbrains.exposed.v1.core.statements.StatementContext
import java.time.LocalDateTime

class AuditInterceptor: GlobalStatementInterceptor {
    @OptIn(InternalApi::class)
    override fun beforeExecution(transaction: Transaction, context: StatementContext) {
        val userContext = SecurityContextHolder.getContext() ?: return
        val user = userContext.name
        val statement = context.statement
        val currentTime = LocalDateTime.now()

        //TODO: May use it for audit log table

//        when(statement.type) {
//            StatementType.INSERT -> {
//                val mainStatement = when (statement) {
//                    is InsertStatement<*> -> {
//                        statement
//                    }
//
//                    is ReturningStatement if statement.mainStatement is InsertStatement<*> -> {
//                        statement.mainStatement as InsertStatement<*>
//                    }
//
//                    else -> return
//                }
//            }
//
//            StatementType.UPDATE -> {
//                val mainStatement = when (statement) {
//                    is UpdateStatement -> {
//                        statement
//                    }
//
//                    is ReturningStatement if statement.mainStatement is UpdateStatement -> {
//                        statement.mainStatement as UpdateStatement
//                    }
//
//                    else -> return
//                }
//
//                mainStatement.targets.filterIsInstance<IAuditColumn>().forEach {
//                    val values = mainStatement.values
//
//                    values[it.modifiedAt] = currentTime
//                    values[it.modifiedBy] = user
//                }
//            }
//
//            else -> {}
//        }
    }
}