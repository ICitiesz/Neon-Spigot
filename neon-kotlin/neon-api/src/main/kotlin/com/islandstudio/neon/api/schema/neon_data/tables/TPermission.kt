/*
 * This file is generated by jOOQ.
 */
package com.islandstudio.neon.api.schema.neon_data.tables


import com.islandstudio.neon.api.schema.neon_data.NeonData
import com.islandstudio.neon.api.schema.neon_data.keys.FK_T_PERMISSION_PARENT_PERMISSION_ID
import com.islandstudio.neon.api.schema.neon_data.keys.FK_T_ROLE_PERMISSION_PERMISSION_ID
import com.islandstudio.neon.api.schema.neon_data.keys.PK_T_PERMISSION
import com.islandstudio.neon.api.schema.neon_data.keys.UQ_T_PERMISSION_PERMISSION_CODE
import com.islandstudio.neon.api.schema.neon_data.tables.TRole.TRolePath
import com.islandstudio.neon.api.schema.neon_data.tables.TRolePermission.TRolePermissionPath
import com.islandstudio.neon.api.schema.neon_data.tables.records.TPermissionRecord
import org.jooq.*
import org.jooq.impl.DSL
import org.jooq.impl.Internal
import org.jooq.impl.SQLDataType
import org.jooq.impl.TableImpl
import java.time.LocalDateTime


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class TPermission(
    alias: Name,
    path: Table<out Record>?,
    childPath: ForeignKey<out Record, TPermissionRecord>?,
    parentPath: InverseForeignKey<out Record, TPermissionRecord>?,
    aliased: Table<TPermissionRecord>?,
    parameters: Array<Field<*>?>?,
    where: Condition?
): TableImpl<TPermissionRecord>(
    alias,
    NeonData.NEON_DATA,
    path,
    childPath,
    parentPath,
    aliased,
    parameters,
    DSL.comment(""),
    TableOptions.table(),
    where,
) {
    companion object {

        /**
         * The reference instance of <code>NEON_DATA.T_PERMISSION</code>
         */
        val T_PERMISSION: TPermission = TPermission()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<TPermissionRecord> = TPermissionRecord::class.java

    /**
     * The column <code>NEON_DATA.T_PERMISSION.PERMISSION_ID</code>.
     */
    val PERMISSION_ID: TableField<TPermissionRecord, Long?> = createField(DSL.name("PERMISSION_ID"), SQLDataType.BIGINT.nullable(false).identity(true), this, "")

    /**
     * The column <code>NEON_DATA.T_PERMISSION.PERMISSION_CODE</code>.
     */
    val PERMISSION_CODE: TableField<TPermissionRecord, String?> = createField(DSL.name("PERMISSION_CODE"), SQLDataType.VARCHAR(64).nullable(false), this, "")

    /**
     * The column <code>NEON_DATA.T_PERMISSION.PERMISSION_DESC</code>.
     */
    val PERMISSION_DESC: TableField<TPermissionRecord, String?> = createField(DSL.name("PERMISSION_DESC"), SQLDataType.VARCHAR(1000000000).nullable(false), this, "")

    /**
     * The column <code>NEON_DATA.T_PERMISSION.PARENT_PERMISSION_ID</code>.
     */
    val PARENT_PERMISSION_ID: TableField<TPermissionRecord, Long?> = createField(DSL.name("PARENT_PERMISSION_ID"), SQLDataType.BIGINT, this, "")

    /**
     * The column <code>NEON_DATA.T_PERMISSION.CREATED_AT</code>.
     */
    val CREATED_AT: TableField<TPermissionRecord, LocalDateTime?> = createField(DSL.name("CREATED_AT"), SQLDataType.LOCALDATETIME(6).nullable(false).defaultValue(DSL.field(DSL.raw("CURRENT_TIMESTAMP"), SQLDataType.LOCALDATETIME)), this, "")

    /**
     * The column <code>NEON_DATA.T_PERMISSION.CREATED_BY</code>.
     */
    val CREATED_BY: TableField<TPermissionRecord, String?> = createField(DSL.name("CREATED_BY"), SQLDataType.VARCHAR(16).nullable(false).defaultValue(DSL.field(DSL.raw("'SYSTEM'"), SQLDataType.VARCHAR)), this, "")

    /**
     * The column <code>NEON_DATA.T_PERMISSION.MODIFIED_AT</code>.
     */
    val MODIFIED_AT: TableField<TPermissionRecord, LocalDateTime?> = createField(DSL.name("MODIFIED_AT"), SQLDataType.LOCALDATETIME(6).nullable(false).defaultValue(DSL.field(DSL.raw("CURRENT_TIMESTAMP"), SQLDataType.LOCALDATETIME)), this, "")

    /**
     * The column <code>NEON_DATA.T_PERMISSION.MODIFIED_BY</code>.
     */
    val MODIFIED_BY: TableField<TPermissionRecord, String?> = createField(DSL.name("MODIFIED_BY"), SQLDataType.VARCHAR(16).nullable(false).defaultValue(DSL.field(DSL.raw("'SYSTEM'"), SQLDataType.VARCHAR)), this, "")

    private constructor(alias: Name, aliased: Table<TPermissionRecord>?): this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<TPermissionRecord>?, parameters: Array<Field<*>?>?): this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<TPermissionRecord>?, where: Condition?): this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>NEON_DATA.T_PERMISSION</code> table reference
     */
    constructor(alias: String): this(DSL.name(alias))

    /**
     * Create an aliased <code>NEON_DATA.T_PERMISSION</code> table reference
     */
    constructor(alias: Name): this(alias, null)

    /**
     * Create a <code>NEON_DATA.T_PERMISSION</code> table reference
     */
    constructor(): this(DSL.name("T_PERMISSION"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, TPermissionRecord>?, parentPath: InverseForeignKey<out Record, TPermissionRecord>?): this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, T_PERMISSION, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class TPermissionPath : TPermission, Path<TPermissionRecord> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, TPermissionRecord>?, parentPath: InverseForeignKey<out Record, TPermissionRecord>?): super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<TPermissionRecord>): super(alias, aliased)
        override fun `as`(alias: String): TPermissionPath = TPermissionPath(DSL.name(alias), this)
        override fun `as`(alias: Name): TPermissionPath = TPermissionPath(alias, this)
        override fun `as`(alias: Table<*>): TPermissionPath = TPermissionPath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else NeonData.NEON_DATA
    override fun getIdentity(): Identity<TPermissionRecord, Long?> = super.getIdentity() as Identity<TPermissionRecord, Long?>
    override fun getPrimaryKey(): UniqueKey<TPermissionRecord> = PK_T_PERMISSION
    override fun getUniqueKeys(): List<UniqueKey<TPermissionRecord>> = listOf(UQ_T_PERMISSION_PERMISSION_CODE)
    override fun getReferences(): List<ForeignKey<TPermissionRecord, *>> = listOf(FK_T_PERMISSION_PARENT_PERMISSION_ID)

    private lateinit var _tPermission: TPermissionPath

    /**
     * Get the implicit join path to the <code>NEON_DATA.T_PERMISSION</code>
     * table.
     */
    fun tPermission(): TPermissionPath {
        if (!this::_tPermission.isInitialized)
            _tPermission = TPermissionPath(this, FK_T_PERMISSION_PARENT_PERMISSION_ID, null)

        return _tPermission;
    }

    val tPermission: TPermissionPath
        get(): TPermissionPath = tPermission()

    private lateinit var _tRolePermission: TRolePermissionPath

    /**
     * Get the implicit to-many join path to the
     * <code>NEON_DATA.T_ROLE_PERMISSION</code> table
     */
    fun tRolePermission(): TRolePermissionPath {
        if (!this::_tRolePermission.isInitialized)
            _tRolePermission = TRolePermissionPath(this, null, FK_T_ROLE_PERMISSION_PERMISSION_ID.inverseKey)

        return _tRolePermission;
    }

    val tRolePermission: TRolePermissionPath
        get(): TRolePermissionPath = tRolePermission()

    /**
     * Get the implicit many-to-many join path to the
     * <code>NEON_DATA.T_ROLE</code> table
     */
    val tRole: TRolePath
        get(): TRolePath = tRolePermission().tRole()
    override fun getChecks(): List<Check<TPermissionRecord>> = listOf(
        Internal.createCheck(this, DSL.name("T_PERMISSION_ENSURE_UPPERCASE"), "\"PERMISSION_CODE\" = UPPER(\"PERMISSION_CODE\")", true)
    )
    override fun `as`(alias: String): TPermission = TPermission(DSL.name(alias), this)
    override fun `as`(alias: Name): TPermission = TPermission(alias, this)
    override fun `as`(alias: Table<*>): TPermission = TPermission(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): TPermission = TPermission(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): TPermission = TPermission(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): TPermission = TPermission(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): TPermission = TPermission(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): TPermission = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): TPermission = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): TPermission = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): TPermission = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): TPermission = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): TPermission = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): TPermission = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): TPermission = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): TPermission = where(DSL.notExists(select))
}
