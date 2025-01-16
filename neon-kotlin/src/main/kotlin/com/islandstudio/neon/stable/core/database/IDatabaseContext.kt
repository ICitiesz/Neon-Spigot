package com.islandstudio.neon.stable.core.database

import com.islandstudio.neon.shared.core.di.IComponentInjector
import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.koin.core.component.inject

interface IDatabaseContext: IComponentInjector {
    fun getDatabaseContext(): DSLContext {
        val dbConnector by inject<DatabaseInterface>()
        val dataSource = dbConnector

        return DSL.using(dataSource.getDataSource(),SQLDialect.HSQLDB)
    }

    fun getDbContextConfiguration(dbContext: DSLContext): Configuration {
        return dbContext.configuration()
    }
}