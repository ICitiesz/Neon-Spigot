package com.islandstudio.neon.api.entity.security

import com.islandstudio.neon.api.entity.IAuditBase
import java.time.LocalDateTime

data class PermissionEntity(
    var permissionId: Long? = null,
    var permissionCode: String? = null,
    var permissionDesc: String? = null,
    var parentPermissionId: Long? = null,
    override var createdAt: LocalDateTime = IAuditBase.defaultTime(),
    override var createdBy: String = IAuditBase.defaultAuditor(),
    override var modifiedAt: LocalDateTime = IAuditBase.defaultTime(),
    override var modifiedBy: String = IAuditBase.defaultAuditor()
): IAuditBase<PermissionEntity>