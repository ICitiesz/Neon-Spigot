package com.islandstudio.neon.api.entity.security

import com.islandstudio.neon.api.entity.IAuditBase
import java.time.LocalDateTime

data class RoleEntity(
    var roleId: Long? = null,
    var roleDisplayName: String? = null,
    var roleCode: String? = null,
    var assignedPlayerCount: Long = 0,
    override var createdAt: LocalDateTime = IAuditBase.defaultTime(),
    override var createdBy: String = IAuditBase.defaultAuditor(),
    override var modifiedAt: LocalDateTime = IAuditBase.defaultTime(),
    override var modifiedBy: String = IAuditBase.defaultAuditor(),
) : IAuditBase<RoleEntity>