package com.islandstudio.neon.shared.datasource

import com.islandstudio.neon.shared.core.di.IComponentInjector
import org.jooq.DSLContext
import org.koin.core.component.inject

interface IDatabaseContext: IComponentInjector {
    fun dbContext(): DSLContext {
        val dslContext by inject<DSLContext>()

        return dslContext
    }

    fun dbConnection(): java.sql.Connection {
        val databaseManager by inject<ConnectionManager>()

        return databaseManager.getDataSource().connection
    }
}