package com.islandstudio.neon.command.properties

data class AccessibleCommand(
    val command: String,
    val accessibleCommandOptions: ArrayList<AccessibleCommandOption>
) {
    data class AccessibleCommandOption(
        val commandOption: String,
        val accessibleCommandOptionArgs: ArrayList<String>
    )
    fun hasAccessibleCommandOption(commandOption: String): Boolean {
        return accessibleCommandOptions.any { it.commandOption.equals(commandOption, true) }
    }

    fun hasAccessibleCommandOptionArg(commandOption: String, commandOptionArg: String): Boolean {
        return accessibleCommandOptions
            .find { it.commandOption.equals(commandOption, true) }
            ?.let {
                it.accessibleCommandOptionArgs.any { x -> x.equals(commandOptionArg, true) }
            } ?: false
    }
}