package com.islandstudio.neon.apinew.connection

import com.islandstudio.neon.apinew.entity.BaseEntity
import com.islandstudio.neon.shared.core.di.IComponentProvider
import org.bukkit.entity.Player

class UserContext(context: Player?): IComponentProvider {
    val contextName = when {
        context == null -> BaseEntity.defaultAuditor
        else -> context.name
    }
}