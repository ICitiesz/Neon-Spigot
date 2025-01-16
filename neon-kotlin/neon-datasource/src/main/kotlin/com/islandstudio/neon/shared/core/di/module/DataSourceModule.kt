package com.islandstudio.neon.shared.core.di.module

import com.islandstudio.neon.shared.datasource.ConnectionManager
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.islandstudio.neon.shared.datasource")
class DataSourceModule {
    @Single
    fun provideDSLContext(connectionManager: ConnectionManager): DSLContext {
        return DSL.using(connectionManager.getDataSource(), SQLDialect.HSQLDB)
    }
}