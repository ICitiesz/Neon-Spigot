package com.islandstudio.neon.api.repository.system.impl

import com.islandstudio.neon.api.IDatabaseContext
import com.islandstudio.neon.api.repository.system.IDatabaseInfoRepository
import org.jooq.impl.DSL
import org.koin.core.annotation.Single

@Single
class DatabaseInfoRepository: IDatabaseInfoRepository, IDatabaseContext {
    override fun existConnected(): Boolean {
        runCatching {
            return dbContext()
                .fetchExists(
                    DSL.table("INFORMATION_SCHEMA.SYSTEM_SESSIONINFO") ,
                    DSL.condition(DSL.field("KEY").eq("CONNECTED"))
                )
        }.getOrThrow()
    }
}