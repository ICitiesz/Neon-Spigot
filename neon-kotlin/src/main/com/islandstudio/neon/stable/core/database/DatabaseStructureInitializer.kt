package com.islandstudio.neon.stable.core.database

import com.islandstudio.neon.stable.core.application.di.IComponentInjector
import com.islandstudio.neon.stable.core.database.schema.neon_data.NeonData
import com.islandstudio.neon.stable.core.database.schema.neon_data.keys.FK_DT_PLAYER_PROFILE_DT_ROLE_ROLE_ID
import com.islandstudio.neon.stable.core.database.schema.neon_data.keys.FK_DT_ROLE_ACCESS_PERMISSION__DT_ACCESS_PERMISSION_PERMISSION_ID
import com.islandstudio.neon.stable.core.database.schema.neon_data.keys.FK_DT_ROLE_ACCESS__DT_ROLE_ROLE_ID
import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.references.DT_ACCESS_PERMISSION
import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.references.DT_PLAYER_PROFILE
import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.references.DT_ROLE
import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.references.DT_ROLE_ACCESS
import org.jooq.*
import org.koin.core.component.inject
import java.util.*
import java.util.logging.Logger

class DatabaseStructureInitializer: IComponentInjector, IDatabaseContext {
    //private val dbContext by inject<DSLContext>()
    private val dbContext = getDatabaseContext()

    fun initializeStructure() {
        runCatching {
            dbContext.createSchemaIfNotExists(NeonData()).execute()

            val tables: ArrayList<Query> = arrayListOf(
                /* DT_ROLE */
                TableBuilder(DT_ROLE, dbContext)
                    .hasUniqueKeys(true)
                    .hasChecks(true)
                    .build(),

                /* DT_PLAYER_PROFILE */
                TableBuilder(DT_PLAYER_PROFILE, dbContext)
                    .hasUniqueKeys(true)
                    .hasForeignKeys(true)
                    .addForeignKeyAction(FK_DT_PLAYER_PROFILE_DT_ROLE_ROLE_ID to ForeignKeyActionType.ON_DELETE_SET_NULL)
                    .build(),

                /* DT_ACCESS_PERMISSION */
                TableBuilder(DT_ACCESS_PERMISSION, dbContext)
                    .hasUniqueKeys(true)
                    .hasChecks(true)
                    .build(),

                /* DT_ROLE_ACCESS */
                TableBuilder(DT_ROLE_ACCESS, dbContext)
                    .hasForeignKeys(true)
                    .hasUniqueKeys(true)
                    .hasChecks(true)
                    .addForeignKeyAction(
                        FK_DT_ROLE_ACCESS__DT_ROLE_ROLE_ID to ForeignKeyActionType.ON_DELETE_CASCADE,
                        FK_DT_ROLE_ACCESS_PERMISSION__DT_ACCESS_PERMISSION_PERMISSION_ID to ForeignKeyActionType.ON_DELETE_CASCADE
                    )
                    .build()
            )

            dbContext.batch(tables).execute()
        }.onFailure {
            it.printStackTrace()
        }.onSuccess {
            val neonLogger by inject<Logger>()

            neonLogger.info("Database initialize successful!")
        }
    }

    /**
     * Used to build table with provided keys (Primary, Unique, Foreign, Check)
     *  Default:
     *  Primary Key = true
     *  Unique Keys = false
     *  Foreign Keys = false
     *  Check Keys = false
     *
     * @constructor table
     *
     * @param dbContext
     */
    private class TableBuilder(private val table: Table<*>, dbContext: DSLContext) {
        private var hasPrimaryKey = true
        private var hasUniqueKeys = false
        private var hasForeignKeys = false
        private var hasChecks = false
        private val foreignKeyAction: TreeMap<String, ForeignKeyActionType> = TreeMap()

        private var tableInitQuery = dbContext
            .createTableIfNotExists(table)
            .tableElements(table.fields().toMutableList())

        /**
         * Set whether it has primary key. Default: true
         *
         * @param newHasPrimaryKey
         * @return TableConstraintBuilder
         */
        fun hasPrimaryKey(newHasPrimaryKey: Boolean): TableBuilder {
            this.hasPrimaryKey = newHasPrimaryKey
            return this
        }

        /**
         * Set whether it has unique keys. Default: false
         *
         * @param newHasUniqueKeys
         * @return TableConstraintBuilder
         */
        fun hasUniqueKeys(newHasUniqueKeys: Boolean): TableBuilder {
            this.hasUniqueKeys = newHasUniqueKeys
            return this
        }

        /**
         * Set whether it has foreign keys. Default: false
         *
         * @param newHasForeignKeys
         * @return TableConstraintBuilder
         */
        fun hasForeignKeys(newHasForeignKeys: Boolean): TableBuilder {
            this.hasForeignKeys = newHasForeignKeys
            return this
        }

        /**
         * Set whether it has checks constraint. Default: false
         *
         * @param newHasChecks
         * @return TableConstraintBuilder
         */
        fun hasChecks(newHasChecks: Boolean): TableBuilder {
            this.hasChecks = newHasChecks
            return this
        }

        fun addForeignKeyAction(vararg constraintAction: Pair<ForeignKey<*, *>, ForeignKeyActionType>): TableBuilder {
            this.foreignKeyAction.putAll(constraintAction.associate { it.first.name to it.second })
            return this
        }

        fun build(): Query {
            if (hasPrimaryKey) {
                tableInitQuery = tableInitQuery.constraints(table.primaryKey?.constraint())
            }

            if (hasUniqueKeys) {
                tableInitQuery = tableInitQuery.constraints(table.uniqueKeys.map { it.constraint() })
            }

            if (hasForeignKeys) {
                foreignKeyAction.let { action ->
                    if (action.isEmpty()) {
                        tableInitQuery = tableInitQuery.constraints(table.references.map { it.constraint() })
                        return@let
                    }

                    tableInitQuery = tableInitQuery.constraints(
                        table.references.map {
                            val foreignKeyOnStep = it.constraint() as ConstraintForeignKeyOnStep

                            if (!action.containsKey(foreignKeyOnStep.name)) return@map foreignKeyOnStep

                            when(action[foreignKeyOnStep.name]!!) {
                                ForeignKeyActionType.ON_DELETE_NO_ACTION -> { foreignKeyOnStep.onDeleteNoAction() }

                                ForeignKeyActionType.ON_DELETE_SET_DEFAULT -> { foreignKeyOnStep.onDeleteSetDefault() }

                                ForeignKeyActionType.ON_DELETE_SET_NULL -> { foreignKeyOnStep.onDeleteSetNull() }

                                ForeignKeyActionType.ON_DELETE_CASCADE -> { foreignKeyOnStep.onDeleteCascade() }

                                ForeignKeyActionType.ON_DELETE_RESTRICT -> { foreignKeyOnStep.onDeleteRestrict() }

                                ForeignKeyActionType.ON_UPDATE_NO_ACTION -> { foreignKeyOnStep.onUpdateNoAction() }

                                ForeignKeyActionType.ON_UPDATE_SET_DEFAULT -> { foreignKeyOnStep.onUpdateSetDefault() }

                                ForeignKeyActionType.ON_UPDATE_SET_NULL -> { foreignKeyOnStep.onUpdateSetNull() }

                                ForeignKeyActionType.ON_UPDATE_CASCADE -> { foreignKeyOnStep.onUpdateCascade() }

                                ForeignKeyActionType.ON_UPDATE_RESTRICT -> { foreignKeyOnStep.onUpdateRestrict() }
                            }

                            return@map foreignKeyOnStep
                        }
                    )
                }
            }

            if (hasChecks) {
                tableInitQuery = tableInitQuery.constraints(table.checks.map { it.constraint() })
            }

            return tableInitQuery
        }
    }
}