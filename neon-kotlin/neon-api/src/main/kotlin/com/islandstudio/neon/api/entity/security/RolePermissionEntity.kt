package com.islandstudio.neon.api.entity.security

import com.islandstudio.neon.api.entity.IAuditBase
import java.time.LocalDateTime

data class RolePermissionEntity(
    var rolePermissionId: Long? = null,
    var roleId: Long? = null,
    var permissionId: Long? = null,
    var parentRolePermissionId: Long? = null,
    override var createdAt: LocalDateTime = IAuditBase.defaultTime(),
    override var createdBy: String = IAuditBase.defaultAuditor(),
    override var modifiedAt: LocalDateTime = IAuditBase.defaultTime(),
    override var modifiedBy: String = IAuditBase.defaultAuditor()
): IAuditBase<RolePermissionEntity>
