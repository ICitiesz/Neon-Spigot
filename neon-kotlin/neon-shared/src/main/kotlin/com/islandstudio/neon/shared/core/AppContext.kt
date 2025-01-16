package com.islandstudio.neon.shared.core

import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.exception.NeonException
import com.islandstudio.neon.shared.core.exception.NeonIncompatibleVersionException
import com.islandstudio.neon.shared.core.exception.NeonLoaderException
import com.islandstudio.neon.shared.core.io.resource.NeonInternalResource
import com.islandstudio.neon.shared.core.io.resource.PluginDetail
import com.islandstudio.neon.shared.core.io.resource.ResourceManager
import com.islandstudio.neon.shared.core.server.ServerProvider
import com.islandstudio.neon.shared.core.server.ServerRunningMode
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.annotation.Single
import org.koin.core.component.inject
import java.util.*

@Single
class AppContext(): IComponentInjector {
    private val pluginInstance by inject<JavaPlugin>()
    private val pluginServer = pluginInstance.server

    private val codeMessages = Properties()
    private val envValues: Dotenv = dotenv {
        this.directory = NeonInternalResource.NeonSharedEnvironmentValue.getDirectoryPath()
        this.filename = NeonInternalResource.NeonSharedEnvironmentValue.getResourceName()
        this.ignoreIfMalformed = true
        this.ignoreIfMissing = true
    }

    private val serverProvider = ServerProvider.entries.first { it.name.equals(pluginServer.name, true) }
    val serverVersion = pluginServer.bukkitVersion.split("-").first()
    val serverMajorVersion = "${serverVersion.split(".")[0]}.${serverVersion.split(".")[1]}"
    val serverRunningMode = if (pluginServer.onlineMode) ServerRunningMode.Online else ServerRunningMode.Offline
    val isVersionCompatible = arrayOf(
        "1.17", "1.17.1",
        "1.18", "1.18.1", "1.18.2",
        "1.19", "1.19.1", "1.19.2", "1.19.3", "1.19.4",
        "1.20", "1.20.1", "1.20.2", "1.20.3", "1.20.4"
    ).run { serverVersion in this }

    fun getAppEnvValue(key: String): String = envValues.get(key)

    fun loadCodeMessages() {
        ResourceManager.getNeonResourceAsStream(NeonInternalResource.NeonSharedCodeMessages).use {
            codeMessages.load(it)
        }
    }

    fun getCodeMessage(code: String): String = codeMessages.getProperty(code) ?: code

    fun getFormattedCodeMessage(code: String, vararg valueReplacement: Any): String {
        val codeMessage = getCodeMessage(code)

        if (codeMessage == code) return codeMessage

        return String.format(codeMessage, *valueReplacement)
    }

    fun ensureVersionCompatible(codeMessage: String = getCodeMessage("neon.error.apploader.neon_load_incompatible_version")): Boolean {
        if (isVersionCompatible) return true
        throw NeonIncompatibleVersionException(codeMessage)
    }

    fun validateServerProvider(serverProvider: ServerProvider): Boolean {
        return this.serverProvider == serverProvider
    }

    fun ensureParentPluginLoaded(): Boolean {
        return ensureRequiredPluginsLoaded(PluginDetail("Neon", "com.islandstudio.neon.Neon"))
    }

    fun getParentPlugin(): Plugin {
        return pluginServer.pluginManager.plugins.find { plugin ->
            plugin.javaClass.name == "com.islandstudio.neon.Neon"
        } ?: throw NeonException("Neon plugin not found!")
    }

    fun ensureRequiredPluginsLoaded(vararg pluginDetails: PluginDetail): Boolean {
        val loadedPlugins = pluginServer.pluginManager.plugins

        pluginDetails.forEach { pluginDetail ->
            val isPluginExist = loadedPlugins.any { loadedPlugin ->
                loadedPlugin.javaClass.name == pluginDetail.mainClassPath && loadedPlugin.name == pluginDetail.pluginName
            }

            if (isPluginExist) return@forEach

            throw NeonLoaderException(
                getFormattedCodeMessage(
                    "neon.exception.apploader.required_plugin_not_found",
                    pluginInstance.name, pluginDetail.pluginName
                )
            )
        }

        return true
    }
}