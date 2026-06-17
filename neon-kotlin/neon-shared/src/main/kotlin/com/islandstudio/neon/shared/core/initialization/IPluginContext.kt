package com.islandstudio.neon.shared.core.initialization

import com.islandstudio.neon.shared.core.server.ServerProvider
import com.islandstudio.neon.shared.core.server.ServerRunningMode
import com.islandstudio.neon.shared.rework.core.io.ResourceManagerRework
import org.bukkit.Server
import org.bukkit.plugin.Plugin
import java.io.File
import java.util.logging.Logger


interface IPluginContext {
    val mainPluginInstance: Plugin
    val mainPluginFile: File

    val serverVersion: String
    val serverMajorVersion: String
    val serverOperationMode: ServerRunningMode

    val resourceManager: ResourceManagerRework

    fun getParentPlugin(): Plugin
    fun getServer(): Server
    fun getPluginLogger(): Logger

    fun isVersionCompatible(): Boolean
    fun isServerProviderCompatible(serverProvider: ServerProvider): Boolean

    suspend fun loadCodeMessages()

    fun getCodeMessage(code: String): String
}