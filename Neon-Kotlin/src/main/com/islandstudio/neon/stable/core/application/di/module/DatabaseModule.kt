package com.islandstudio.neon.stable.core.application.di.module

import com.islandstudio.neon.stable.core.database.DatabaseConnector
import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.islandstudio.neon.stable.core.database")
class DatabaseModule {
    @Single
    fun provideDslContext(): DSLContext {
        return DSL.using(DatabaseConnector.hikariDataSource, SQLDialect.HSQLDB)
    }

    @Single
    fun provideDSLConfiguration(dslContext: DSLContext): Configuration {
        return dslContext.configuration()
    }
}