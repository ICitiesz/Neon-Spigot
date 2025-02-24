package com.islandstudio.neon.command.properties

data class AccessibleCommand(
    val command: String,
    val accessibleCommandOptions: ArrayList<AccessibleCommandOption>
) {
    data class AccessibleCommandOption(
        val commandOption: String,
        val accessibleCommandOptionArgs: ArrayList<String>
    )
}