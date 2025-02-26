package com.islandstudio.neon.command.option

import com.islandstudio.neon.command.CommandAlias
import com.islandstudio.neon.command.properties.AbstractCommandOption
import com.islandstudio.neon.command.properties.AbstractCommandOptionArgument

sealed class RoleCommandOption: AbstractCommandOption<RoleCommandOption.RoleCommandOptionArgument>(CommandAlias.RoleAlias) {
    sealed class RoleCommandOptionArgument(commandOption: RoleCommandOption): AbstractCommandOptionArgument(commandOption) {
        data object UnderscoreAsSpace: RoleCommandOptionArgument(Create) {
            override val optionArg: String = "underscoreAsSpace"
            override val optionArgIndex: Int = 4
            override val inheritPermission: Boolean = true
        }
    }

    data object Create: RoleCommandOption() {
        override val option: String = "create"
        override val optionIndex: Int = 1
        override val inheritPermission: Boolean = true
        override val optionArguments: ArrayList<RoleCommandOptionArgument> = getAllCommandOptionArguments(RoleCommandOptionArgument::class)
    }

    data object Remove: RoleCommandOption() {
        override val option: String = "remove"
        override val optionIndex: Int = 1
        override val inheritPermission: Boolean = true
    }

    data object Assign: RoleCommandOption() {
        override val option: String = "assign"
        override val optionIndex: Int = 1
        override val inheritPermission: Boolean = true
    }

    data object Unassign: RoleCommandOption() {
        override val option: String = "unassign"
        override val optionIndex: Int = 1
        override val inheritPermission: Boolean = true
    }
}