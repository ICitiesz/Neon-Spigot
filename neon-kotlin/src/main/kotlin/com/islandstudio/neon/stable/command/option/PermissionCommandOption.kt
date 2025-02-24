package com.islandstudio.neon.stable.command.option

import com.islandstudio.neon.stable.command.properties.AbstractCommandOption
import com.islandstudio.neon.stable.command.properties.CommandAlias

sealed class PermissionCommandOption: AbstractCommandOption<Nothing>(CommandAlias.PermissionAlias) {
    data object Grant: PermissionCommandOption() {
        override val option: String = "grant"
        override val optionIndex: Int = 1
        override val inheritPermission: Boolean = true
    }

    data object Revoke: PermissionCommandOption() {
        override val option: String = "revoke"
        override val optionIndex: Int = 1
        override val inheritPermission: Boolean = true
    }

    data object RevokeAll: PermissionCommandOption() {
        override val option: String = "revokeAll"
        override val optionIndex: Int = 1
        override val inheritPermission: Boolean = true
    }
}