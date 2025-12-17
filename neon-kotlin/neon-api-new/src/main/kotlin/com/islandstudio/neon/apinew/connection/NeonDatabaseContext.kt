package com.islandstudio.neon.apinew.connection

import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL

class NeonDatabaseContext(dataSource: NeonDataSource): DSLContext by DSL.using(dataSource, SQLDialect.MARIADB) {
}