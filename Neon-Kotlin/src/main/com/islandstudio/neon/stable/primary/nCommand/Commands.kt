package com.islandstudio.neon.stable.primary.nCommand

import org.simpleyaml.configuration.MemorySection

open class Commands {
    companion object {
        fun validateArgument(arg: String, commandArgument: CommandArgument, ignoreCase: Boolean = true): Boolean {
            return arg.equals(commandArgument.argument, ignoreCase)
        }
    }

    enum class CommandAlias(val aliasName: String) {
        WAYPOINTS("waypoints"),
        RANK("rank"),
        DEBUG("debug"),
        GM("gm"),
        REGEN("regen"),
        SERVERFEATURES("serverfeatures"),
        EFFECT("effect"),
        MOD("mod"),
        NFIREWORKS("fireworks"),
        DURABILITY("durability"),
        NPAINTING("painting")
    }

    enum class CommandArgument(val argument: String) {
        CREATE("create"),
        REMOVE("remove"),
        ALL("all"),
        PAINTING_REMOVAL_STICK("removalStick");
    }

    enum class CommandTargetUser {
        ADMIN,
        PLAYER
    }

    object CommandUIBookProperties {
        const val BOOK_AUTHOR = "ICities"
        const val COMMAND_LIST_BOOK_TITLE = "Neon Command List"
        const val COMMAND_USAGE_BOOK_TITLE = "Neon Command Usages"
    }
    data class CommandDetail(private val commandData: Map.Entry<String, Any>) {
        private val commandProperties: MemorySection = commandData.value as MemorySection
        val commandName: String = commandData.key
        val commandWithPrefix: String = commandProperties.getString("withPrefix")
        val commandUsageList: ArrayList<CommandUsage> = ArrayList()

        init {
            commandProperties.getList("usages").forEach {
                commandUsageList.add(CommandUsage(it as LinkedHashMap<*, *>))
            }
        }

        data class CommandUsage(private val commandUsageData: LinkedHashMap<*, *>) {
            val usageSyntax: String = commandUsageData["syntax"] as String
            val usageDescription: String = commandUsageData["description"] as String
        }
    }
}