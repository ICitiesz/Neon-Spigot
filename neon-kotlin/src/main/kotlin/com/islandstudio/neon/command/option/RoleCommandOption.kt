package com.islandstudio.neon.command.option

import com.islandstudio.neon.command.CommandAlias
import com.islandstudio.neon.command.properties.AbstractCommandOption
import com.islandstudio.neon.command.properties.AbstractCommandOptionArgument

sealed class RoleCommandOption: AbstractCommandOption<RoleCommandOption.RoleCommandOptionArgument>(CommandAlias.RoleAlias) {
    sealed class RoleCommandOptionArgument(commandOption: RoleCommandOption): AbstractCommandOptionArgument(commandOption) {
        data object CreateUnderscoreAsSpace: RoleCommandOptionArgument(Create) {
            override val optionArg: String = "underscoreAsSpace"
            override val optionArgIndex: Int = 4
            override val inheritPermission: Boolean = true
        }

        data object UpdateUnderscoreAsSpace: RoleCommandOptionArgument(RoleDisplayName) {
            override val optionArg: String = "underscoreAsSpace"
            override val optionArgIndex: Int = 5
            override val inheritPermission: Boolean = true
        }
    }

    data object Create: RoleCommandOption() {
        override val option: String = "create"
        override val optionIndex: Int = 1
        override val inheritPermission: Boolean = true
        override val optionArguments: ArrayList<RoleCommandOptionArgument>
            get() = getAllCommandOptionArguments(RoleCommandOptionArgument::class)
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

    data object Update: RoleCommandOption() {
        override val option: String = "update"
        override val optionIndex: Int = 1
        override val inheritPermission: Boolean = true
    }

    data object RoleCode: RoleCommandOption() {
        override val option: String = "rolecode"
        override val optionIndex: Int = 2
        override val inheritPermission: Boolean = true
    }

    data object RoleDisplayName: RoleCommandOption() {
        override val option: String = "roledisplayname"
        override val optionIndex: Int = 2
        override val inheritPermission: Boolean = true
        override val optionArguments: ArrayList<RoleCommandOptionArgument>
            get() = getAllCommandOptionArguments(RoleCommandOptionArgument::class)
    }
}