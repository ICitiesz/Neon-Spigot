package com.islandstudio.neon.api.entity.player

import com.islandstudio.neon.api.entity.IAuditBase
import java.time.LocalDateTime
import java.util.*

data class PlayerProfileEntity(
    var id: Long? = null,
    var playerUuid: UUID? = null,
    var playerName: String? = null,
    var joinAt: LocalDateTime? = null,
    var roleId: Long? = null,
    override var createdAt: LocalDateTime = IAuditBase.defaultTime(),
    override var createdBy: String = IAuditBase.defaultAuditor(),
    override var modifiedAt: LocalDateTime = IAuditBase.defaultTime(),
    override var modifiedBy: String = IAuditBase.defaultAuditor()
): IAuditBase<PlayerProfileEntity>