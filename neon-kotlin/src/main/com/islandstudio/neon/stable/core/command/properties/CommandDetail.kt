package com.islandstudio.neon.stable.core.command.properties

import org.simpleyaml.configuration.MemorySection

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
