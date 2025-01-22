package com.islandstudio.neon.api

import com.islandstudio.neon.shared.core.di.IComponentInjector
import org.jooq.DSLContext
import org.koin.core.component.inject
import java.sql.Connection

interface IDatabaseContext: IComponentInjector {
    fun dbContext(): DSLContext {
        val dslContext by inject<DSLContext>()

        return dslContext
    }

    fun dbConnection(): Connection {
        val connection by inject<Connection>()

        return connection
    }
}