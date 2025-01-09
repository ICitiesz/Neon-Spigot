package com.islandstudio.neon.stable.core.database

import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.stable.core.database.model.InactiveTable
import com.islandstudio.neon.stable.core.database.model.TableConstraint
import com.islandstudio.neon.stable.core.database.schema.neon_data.NeonData
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.Table
import org.jooq.UniqueKey
import org.jooq.impl.DSL
import org.jooq.meta.ForeignKeyDefinition

class DatabaseStructureInterface: IComponentInjector, IDatabaseContext {
    //private val dbContext by inject<DSLContext>()
    private val dbContext = getDatabaseContext()

    internal class DbTableBuilder(table: Table<*>, dbContext: DSLContext) {
        private var tableInitQuery = dbContext
            .createTableIfNotExists(table)
            .tableElements(table.fields().toMutableList())

        fun <T: Record> withPrimaryKey(primaryKey: UniqueKey<T>): DbTableBuilder {
            tableInitQuery = tableInitQuery.constraint(primaryKey.constraint())
            return this
        }

        fun <T: Record> withUniqueKeys(vararg uniqueKeys: UniqueKey<T>): DbTableBuilder {
            tableInitQuery = tableInitQuery.constraints(uniqueKeys.map { uniqueKey -> uniqueKey.constraint() })
            return this
        }

        fun withForeignKeys(): DbTableBuilder {
            return this
        }

        fun withChecks(): DbTableBuilder {
            return this
        }
    }

    fun testPrint() {

    }


    fun initializeDbStructure() {
        runCatching {
            dbContext.createSchemaIfNotExists(NeonData.NEON_DATA).execute()

            val tables = NeonData.NEON_DATA.tables

            tables.forEach { table ->
                val dbTableBuilder = DbTableBuilder(table, dbContext)

                val primaryKey = table.primaryKey as UniqueKey<Record>
                val uniqueKeys = table.uniqueKeys as List<UniqueKey<Record>>
                val foreignKeys = table.references

                dbTableBuilder.withPrimaryKey(primaryKey)

                if (uniqueKeys.isNotEmpty()) {
                    dbTableBuilder.withUniqueKeys(*uniqueKeys.toTypedArray())
                }

                if (foreignKeys.isNotEmpty()) {
                    foreignKeys.forEach { t ->
                        (t as ForeignKeyDefinition).overload
                    }
                }
            }
        }
    }

    // TODO: The table schema will be changed later
    fun dropInactiveTable() {
        val activeTableNames = NeonData.NEON_DATA.tables.map { table -> table.name }
        val inactiveTableNames = arrayListOf<String>()

        val tableCatalogField = DSL.field("TABLE_CATALOG")
        val tableSchemaField = DSL.field("TABLE_SCHEMA")
        val tableNameField = DSL.field("TABLE_NAME")
        val constraintNameField = DSL.field("CONSTRAINT_NAME")
        val constraintTypeField = DSL.field("CONSTRAINT_TYPE")

        val inactiveTableQuery = dbContext
            .select(tableCatalogField, tableSchemaField, tableNameField)
            .from(DSL.table("INFORMATION_SCHEMA.TABLES"))
            .where(tableCatalogField.eq("NEON_DB")
                .and(tableSchemaField.eq("PUBLIC"))
                .and(tableNameField.notIn(activeTableNames))
            )

        val tableConstraintQuery = dbContext
            .select(tableCatalogField, tableSchemaField, tableNameField, constraintNameField, constraintTypeField)
            .from(DSL.table("INFORMATION_SCHEMA.TABLE_CONSTRAINTS"))

        val inactiveTables = dbContext.fetch(inactiveTableQuery)
            .into(InactiveTable::class.java)
            .apply {
                inactiveTableNames.addAll(this.map { table -> table.tableName!! })
            }

        val tableConstraints = dbContext.fetch(
            tableConstraintQuery.where(tableCatalogField.eq("NEON_DB")
                .and(tableSchemaField.eq("PUBLIC"))
                .and(constraintTypeField.eq("FOREIGN KEY"))
                .and(tableNameField.`in`(inactiveTableNames))
            )).into(TableConstraint::class.java)

        tableConstraints.forEach { constraint ->
            dbContext.alterTable(DSL.table(constraint.tableName))
                .drop(DSL.constraint(constraint.constraintName)).execute()
        }

        inactiveTables.forEach { table ->
            dbContext.dropTableIfExists(DSL.table(table.tableName)).execute()
        }
    }
}