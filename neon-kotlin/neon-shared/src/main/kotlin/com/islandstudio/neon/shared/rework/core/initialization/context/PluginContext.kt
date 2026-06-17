package com.islandstudio.neon.shared.rework.core.initialization.context

import com.islandstudio.neon.shared.core.exception.NeonException
import com.islandstudio.neon.shared.core.io.resource.NeonInternalResource
import com.islandstudio.neon.shared.core.server.ServerProvider
import com.islandstudio.neon.shared.core.server.ServerRunningMode
import com.islandstudio.neon.shared.rework.core.io.ResourceManagerRework
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.Server
import org.bukkit.plugin.Plugin
import org.koin.core.annotation.Single
import java.io.File
import java.util.*
import java.util.logging.Logger


@Single
class PluginContext(
    override val mainPluginInstance: Plugin,
    override val mainPluginFile: File
): IPluginContext {
    override val serverVersion: String = getServer().bukkitVersion.split("-").first()
    override val serverMajorVersion: String = "${serverVersion.split(".")[0]}.${serverVersion.split(".")[1]}"
    override val serverOperationMode: ServerRunningMode = if (getServer().onlineMode) ServerRunningMode.Online else ServerRunningMode.Offline
    override val resourceManager = ResourceManagerRework(this)

    private val compatibleVersions = arrayOf(
        "1.17", "1.17.1",
        "1.18", "1.18.1", "1.18.2",
        "1.19", "1.19.1", "1.19.2", "1.19.3", "1.19.4",
        "1.20", "1.20.1", "1.20.2", "1.20.3", "1.20.4"
    )

    private val codeMessages = Properties()

    override fun getParentPlugin(): Plugin {
        return if (mainPluginInstance.javaClass.name == "com.islandstudio.neon.Neon") {
            mainPluginInstance
        } else {
            mainPluginInstance.server.pluginManager.plugins.find { plugin ->
                plugin.javaClass.name == "com.islandstudio.neon.Neon"
            } ?: throw NeonException("Neon plugin not found!")
        }
    }

    override fun getServer(): Server = mainPluginInstance.server

    override fun getPluginLogger(): Logger = mainPluginInstance.logger

    override fun isVersionCompatible(): Boolean = serverVersion in compatibleVersions

    override fun isServerProviderCompatible(serverProvider: ServerProvider): Boolean {
        //TODO: Pending implementation
        return true
    }

    override suspend fun loadCodeMessages() {
        return withContext(Dispatchers.IO) {
            resourceManager.getNeonResourceAsStream(NeonInternalResource.NeonSharedCodeMessages).use {
                codeMessages.load(it)
            }
        }
    }

    override fun getCodeMessage(code: String): String {
        return codeMessages.getProperty(code) ?: code
    }
}