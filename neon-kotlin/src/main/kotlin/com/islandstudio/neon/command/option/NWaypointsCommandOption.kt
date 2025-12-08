package com.islandstudio.neon.command.option

import com.islandstudio.neon.command.CommandAlias
import com.islandstudio.neon.command.properties.AbstractCommandOption

sealed class NWaypointsCommandOption: AbstractCommandOption<Nothing>(CommandAlias.NWaypointsAlias) {
    data object Add: NWaypointsCommandOption() {
        override val option: String = "add"
        override val optionIndex: Int = 1
        override val inheritPermission: Boolean = true
    }

    data object Remove: NWaypointsCommandOption() {
        override val option: String = "add"
        override val optionIndex: Int = 1
        override val inheritPermission: Boolean = true
    }
}