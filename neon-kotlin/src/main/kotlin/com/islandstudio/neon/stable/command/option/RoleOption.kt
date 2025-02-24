package com.islandstudio.neon.stable.command.option

import com.islandstudio.neon.stable.command.properties.AbstractCommandOption
import com.islandstudio.neon.stable.command.properties.AbstractCommandOptionArgument
import com.islandstudio.neon.stable.command.properties.CommandAlias

sealed class RoleOption: AbstractCommandOption<RoleOption.RoleOptionArgument>(CommandAlias.RoleAlias) {
    sealed class RoleOptionArgument(commandOption: RoleOption): AbstractCommandOptionArgument(commandOption) {
        data object UnderscoreAsSpace: RoleOptionArgument(Create) {
            override val optionArg: String = "underscoreAsSpace"
            override val optionArgIndex: Int = 4
            override val inheritPermission: Boolean = true
        }
    }

    data object Create: RoleOption() {
        override val option: String = "create"
        override val optionIndex: Int = 1
        override val inheritPermission: Boolean = true
        override val optionArguments: ArrayList<RoleOptionArgument> = getAllCommandOptionArguments(RoleOptionArgument::class)
    }

    data object Remove: RoleOption() {
        override val option: String = "remove"
        override val optionIndex: Int = 1
        override val inheritPermission: Boolean = true
    }

    data object Assign: RoleOption() {
        override val option: String = "assign"
        override val optionIndex: Int = 1
        override val inheritPermission: Boolean = true
    }

    data object Unassign: RoleOption() {
        override val option: String = "unassign"
        override val optionIndex: Int = 1
        override val inheritPermission: Boolean = true
    }
}