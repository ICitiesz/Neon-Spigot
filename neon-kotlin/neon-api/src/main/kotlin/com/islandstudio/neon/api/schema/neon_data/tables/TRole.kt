/*
 * This file is generated by jOOQ.
 */
package com.islandstudio.neon.api.schema.neon_data.tables


import com.islandstudio.neon.api.schema.neon_data.NeonData
import com.islandstudio.neon.api.schema.neon_data.keys.FK_T_PLAYER_PROFILE_T_ROLE_ROLE_ID
import com.islandstudio.neon.api.schema.neon_data.keys.FK_T_ROLE_PERMISSION_ROLE_ID
import com.islandstudio.neon.api.schema.neon_data.keys.PK_T_ROLE
import com.islandstudio.neon.api.schema.neon_data.keys.UQ_T_ROLE_ROLE_CODE
import com.islandstudio.neon.api.schema.neon_data.tables.TPermission.TPermissionPath
import com.islandstudio.neon.api.schema.neon_data.tables.TPlayerProfile.TPlayerProfilePath
import com.islandstudio.neon.api.schema.neon_data.tables.TRolePermission.TRolePermissionPath
import com.islandstudio.neon.api.schema.neon_data.tables.records.TRoleRecord
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
open class TRole(
    alias: Name,
    path: Table<out Record>?,
    childPath: ForeignKey<out Record, TRoleRecord>?,
    parentPath: InverseForeignKey<out Record, TRoleRecord>?,
    aliased: Table<TRoleRecord>?,
    parameters: Array<Field<*>?>?,
    where: Condition?
): TableImpl<TRoleRecord>(
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
         * The reference instance of <code>NEON_DATA.T_ROLE</code>
         */
        val T_ROLE: TRole = TRole()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<TRoleRecord> = TRoleRecord::class.java

    /**
     * The column <code>NEON_DATA.T_ROLE.ROLE_ID</code>.
     */
    val ROLE_ID: TableField<TRoleRecord, Long?> = createField(DSL.name("ROLE_ID"), SQLDataType.BIGINT.nullable(false).identity(true), this, "")

    /**
     * The column <code>NEON_DATA.T_ROLE.ROLE_DISPLAY_NAME</code>.
     */
    val ROLE_DISPLAY_NAME: TableField<TRoleRecord, String?> = createField(DSL.name("ROLE_DISPLAY_NAME"), SQLDataType.VARCHAR(64).nullable(false), this, "")

    /**
     * The column <code>NEON_DATA.T_ROLE.ROLE_CODE</code>.
     */
    val ROLE_CODE: TableField<TRoleRecord, String?> = createField(DSL.name("ROLE_CODE"), SQLDataType.VARCHAR(64).nullable(false), this, "")

    /**
     * The column <code>NEON_DATA.T_ROLE.CREATED_AT</code>.
     */
    val CREATED_AT: TableField<TRoleRecord, LocalDateTime?> = createField(DSL.name("CREATED_AT"), SQLDataType.LOCALDATETIME(6).nullable(false).defaultValue(DSL.field(DSL.raw("CURRENT_TIMESTAMP"), SQLDataType.LOCALDATETIME)), this, "")

    /**
     * The column <code>NEON_DATA.T_ROLE.CREATED_BY</code>.
     */
    val CREATED_BY: TableField<TRoleRecord, String?> = createField(DSL.name("CREATED_BY"), SQLDataType.VARCHAR(16).nullable(false).defaultValue(DSL.field(DSL.raw("'SYSTEM'"), SQLDataType.VARCHAR)), this, "")

    /**
     * The column <code>NEON_DATA.T_ROLE.MODIFIED_AT</code>.
     */
    val MODIFIED_AT: TableField<TRoleRecord, LocalDateTime?> = createField(DSL.name("MODIFIED_AT"), SQLDataType.LOCALDATETIME(6).nullable(false).defaultValue(DSL.field(DSL.raw("CURRENT_TIMESTAMP"), SQLDataType.LOCALDATETIME)), this, "")

    /**
     * The column <code>NEON_DATA.T_ROLE.MODIFIED_BY</code>.
     */
    val MODIFIED_BY: TableField<TRoleRecord, String?> = createField(DSL.name("MODIFIED_BY"), SQLDataType.VARCHAR(16).nullable(false).defaultValue(DSL.field(DSL.raw("'SYSTEM'"), SQLDataType.VARCHAR)), this, "")

    private constructor(alias: Name, aliased: Table<TRoleRecord>?): this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<TRoleRecord>?, parameters: Array<Field<*>?>?): this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<TRoleRecord>?, where: Condition?): this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>NEON_DATA.T_ROLE</code> table reference
     */
    constructor(alias: String): this(DSL.name(alias))

    /**
     * Create an aliased <code>NEON_DATA.T_ROLE</code> table reference
     */
    constructor(alias: Name): this(alias, null)

    /**
     * Create a <code>NEON_DATA.T_ROLE</code> table reference
     */
    constructor(): this(DSL.name("T_ROLE"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, TRoleRecord>?, parentPath: InverseForeignKey<out Record, TRoleRecord>?): this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, T_ROLE, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class TRolePath : TRole, Path<TRoleRecord> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, TRoleRecord>?, parentPath: InverseForeignKey<out Record, TRoleRecord>?): super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<TRoleRecord>): super(alias, aliased)
        override fun `as`(alias: String): TRolePath = TRolePath(DSL.name(alias), this)
        override fun `as`(alias: Name): TRolePath = TRolePath(alias, this)
        override fun `as`(alias: Table<*>): TRolePath = TRolePath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else NeonData.NEON_DATA
    override fun getIdentity(): Identity<TRoleRecord, Long?> = super.getIdentity() as Identity<TRoleRecord, Long?>
    override fun getPrimaryKey(): UniqueKey<TRoleRecord> = PK_T_ROLE
    override fun getUniqueKeys(): List<UniqueKey<TRoleRecord>> = listOf(UQ_T_ROLE_ROLE_CODE)

    private lateinit var _tPlayerProfile: TPlayerProfilePath

    /**
     * Get the implicit to-many join path to the
     * <code>NEON_DATA.T_PLAYER_PROFILE</code> table
     */
    fun tPlayerProfile(): TPlayerProfilePath {
        if (!this::_tPlayerProfile.isInitialized)
            _tPlayerProfile = TPlayerProfilePath(this, null, FK_T_PLAYER_PROFILE_T_ROLE_ROLE_ID.inverseKey)

        return _tPlayerProfile;
    }

    val tPlayerProfile: TPlayerProfilePath
        get(): TPlayerProfilePath = tPlayerProfile()

    private lateinit var _tRolePermission: TRolePermissionPath

    /**
     * Get the implicit to-many join path to the
     * <code>NEON_DATA.T_ROLE_PERMISSION</code> table
     */
    fun tRolePermission(): TRolePermissionPath {
        if (!this::_tRolePermission.isInitialized)
            _tRolePermission = TRolePermissionPath(this, null, FK_T_ROLE_PERMISSION_ROLE_ID.inverseKey)

        return _tRolePermission;
    }

    val tRolePermission: TRolePermissionPath
        get(): TRolePermissionPath = tRolePermission()

    /**
     * Get the implicit many-to-many join path to the
     * <code>NEON_DATA.T_PERMISSION</code> table
     */
    val tPermission: TPermissionPath
        get(): TPermissionPath = tRolePermission().tPermission()
    override fun getChecks(): List<Check<TRoleRecord>> = listOf(
        Internal.createCheck(this, DSL.name("T_ROLE_ENSURE_UPPER"), "\"ROLE_CODE\" = UPPER(\"ROLE_CODE\")", true)
    )
    override fun `as`(alias: String): TRole = TRole(DSL.name(alias), this)
    override fun `as`(alias: Name): TRole = TRole(alias, this)
    override fun `as`(alias: Table<*>): TRole = TRole(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): TRole = TRole(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): TRole = TRole(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): TRole = TRole(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): TRole = TRole(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): TRole = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): TRole = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): TRole = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): TRole = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): TRole = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): TRole = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): TRole = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): TRole = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): TRole = where(DSL.notExists(select))
}
