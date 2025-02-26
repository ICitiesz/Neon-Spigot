package com.islandstudio.neon.command.option

import com.islandstudio.neon.command.CommandAlias
import com.islandstudio.neon.command.properties.AbstractCommandOption
import com.islandstudio.neon.command.properties.AbstractCommandOptionArgument

sealed class ServerFeaturesCommandOption: AbstractCommandOption<ServerFeaturesCommandOption.ServerFeaturesCommandOptionArgument>(CommandAlias.ServerFeaturesAlias) {
    sealed class ServerFeaturesCommandOptionArgument(commandOption: ServerFeaturesCommandOption): AbstractCommandOptionArgument(commandOption) {
        data object SetToggleDefault: ServerFeaturesCommandOptionArgument(SetToggle) {
            override val optionArg: String = "default"
            override val optionArgIndex: Int = 3
            override val inheritPermission: Boolean = true
        }

        data object SetOptionDefault: ServerFeaturesCommandOptionArgument(SetOption) {
            override val optionArg: String = "default"
            override val optionArgIndex: Int = 4
            override val inheritPermission: Boolean = true
        }
    }

    data object GetToggle: ServerFeaturesCommandOption() {
        override val option: String = "getToggle"
        override val optionIndex: Int = 1
        override val inheritPermission: Boolean = true
    }

    data object SetToggle: ServerFeaturesCommandOption() {
        override val option: String = "setToggle"
        override val optionIndex: Int = 1
        override val inheritPermission: Boolean = true
    }

    data object GetOption: ServerFeaturesCommandOption() {
        override val option: String = "getOption"
        override val optionIndex: Int = 1
        override val inheritPermission: Boolean = true
    }

    data object SetOption: ServerFeaturesCommandOption() {
        override val option: String = "setOption"
        override val optionIndex: Int = 1
        override val inheritPermission: Boolean = true
    }
}