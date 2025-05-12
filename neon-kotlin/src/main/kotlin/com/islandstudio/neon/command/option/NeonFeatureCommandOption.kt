package com.islandstudio.neon.command.option

import com.islandstudio.neon.command.CommandAlias
import com.islandstudio.neon.command.properties.AbstractCommandOption
import com.islandstudio.neon.command.properties.AbstractCommandOptionArgument

sealed class NeonFeatureCommandOption: AbstractCommandOption<NeonFeatureCommandOption.NeonFeatureCommandOptionArgument>(CommandAlias.NeonFeatureAlias) {
    sealed class NeonFeatureCommandOptionArgument(commandOption: NeonFeatureCommandOption): AbstractCommandOptionArgument(commandOption) {
        data object SetToggleDefault: NeonFeatureCommandOptionArgument(SetToggle) {
            override val optionArg: String = "default"
            override val optionArgIndex: Int = 3
            override val inheritPermission: Boolean = true
        }

        data object SetOptionDefault: NeonFeatureCommandOptionArgument(SetOption) {
            override val optionArg: String = "default"
            override val optionArgIndex: Int = 4
            override val inheritPermission: Boolean = true
        }
    }

    data object GetToggle: NeonFeatureCommandOption() {
        override val option: String = "getToggle"
        override val optionIndex: Int = 1
        override val inheritPermission: Boolean = true
    }

    data object SetToggle: NeonFeatureCommandOption() {
        override val option: String = "setToggle"
        override val optionIndex: Int = 1
        override val inheritPermission: Boolean = true
    }

    data object GetOption: NeonFeatureCommandOption() {
        override val option: String = "getOption"
        override val optionIndex: Int = 1
        override val inheritPermission: Boolean = true
    }

    data object SetOption: NeonFeatureCommandOption() {
        override val option: String = "setOption"
        override val optionIndex: Int = 1
        override val inheritPermission: Boolean = true
    }
}