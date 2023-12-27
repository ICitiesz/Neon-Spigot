package com.islandstudio.neon.stable.core.init

import com.islandstudio.neon.Neon
import com.islandstudio.neon.stable.utils.NItemHighlight
import com.islandstudio.neon.stable.utils.identifier.NeonKeyGeneral
import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture
import org.bukkit.ChatColor
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin.getPlugin
import java.lang.reflect.Field
import java.util.logging.Logger

object NConstructor {
    val plugin: Plugin = getPlugin(Neon::class.java)
    val pluginLogger: Logger = plugin.logger
    val isUsingPaperMC: Boolean = runCatching {
        Class.forName("io.papermc.paper.plugin.loader.PluginLoader")
    }.mapCatching { true }.getOrElse { false }


    private const val NEON_VERSION: String = "|--------------== Neon v1.10.1-rc_2 ==--------------|"
    private val rawVersion: String = plugin.server.bukkitVersion.split("-")[0]
    private val version: String = rawVersion.split(".")[0] + "." + rawVersion.split(".")[1]

    private val supportedVersion = listOf("1.17", "1.18", "1.19", "1.20")
    private val supportedLatestVersions = listOf("1.20", "1.20.1", "1.20.2")

    private val isCompatible: Boolean = version.run {
        if (this !in supportedVersion) return@run false

        if (this == "1.20") {
            if (rawVersion !in supportedLatestVersions) return@run false
        }

        true
    }

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    fun preConstructPlugin() {
        CoroutineScope(newSingleThreadContext("Neon Pre-construction")).async {
            pluginLogger.info("Start filling up Neon......")

            delay(500L)

            if (!isCompatible) {
                pluginLogger.severe("Neon could not be filled up: Incompatible Minecraft version!")
                return@async
            }

            val preloadClasses = NClassProperties.preloadNClasses

            preloadClasses.forEachIndexed { index, clazz ->
                val loadingProgress: Int = (((index + 1).toDouble() / preloadClasses.size.toDouble()) * 100).toInt()

                /* Check if the simple name of the class is equal to "Handler" or "Companion",
                * if so, it split the canonical name and get the last 2 parts.
                * E.g.: com.islandstudio.neon.stable.primary.nCommand.NCommand.Companion -> NCommand.Companion
                */
                val className: String = when {
                    (clazz.simpleName == "Handler" || clazz.simpleName == "Companion") -> {
                        val splitClassNames = clazz.canonicalName.split(".")
                        "${splitClassNames[splitClassNames.size - 2]}.${splitClassNames[splitClassNames.size - 1]}"
                    }

                    else -> {
                        clazz.simpleName
                    }
                }

                runCatching {
                    when {
                        (className.contains("Companion")) -> {
                            clazz.getDeclaredMethod("run").invoke(clazz.enclosingClass.getField("Companion").get(null))
                        }

                        else -> {
                            clazz.getDeclaredMethod("run").invoke(clazz.getField("INSTANCE").get(null))
                        }
                    }
                }.onFailure {
                    pluginLogger.severe(it.stackTraceToString())
                }.onSuccess {
                    pluginLogger.info("Filling up Neon......${loadingProgress}%")
                    delay(150L)
                }
            }

            pluginLogger.info("Neon has been filled up!")
        }.asCompletableFuture().get()
    }

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    fun postConstructPlugin() {
        if (!isCompatible) {
            pluginLogger.severe("Neon could not be lit up: Incompatible Minecraft version!")
            pluginLogger.severe("Please check for the latest version of Neon plugin!")
            pluginLogger.warning("Supported Minecraft version: 1.17.X ~ 1.20.2")
            return
        }

        pluginLogger.info("Start lighting up Neon......")

        Thread.sleep(500L)

        registerItemHighlight()

        val postLoadClasses = NClassProperties.postloadClasses

        postLoadClasses.forEachIndexed { index, clazz ->
            val loadingProgress: Int = (((index + 1).toDouble() / postLoadClasses.size.toDouble()) * 100).toInt()

            /* Check if the simple name of the class is equal to "Handler" or "Companion",
            * if so, it split the canonical name and get the last 2 parts.
            * E.g.: com.islandstudio.neon.stable.primary.nCommand.NCommand.Companion -> NCommand.Companion
            */
            val className: String = when {
                (clazz.simpleName == "Handler" || clazz.simpleName == "Companion") -> {
                    val splitClassNames = clazz.canonicalName.split(".")
                    "${splitClassNames[splitClassNames.size - 2]}.${splitClassNames[splitClassNames.size - 1]}"
                }

                else -> {
                    clazz.simpleName
                }
            }

            if (clazz in NClassProperties.syncClasses) {
                runCatching {
                    when {
                        (className.contains("Companion")) -> {
                            clazz.getDeclaredMethod("run").invoke(clazz.enclosingClass.getField("Companion").get(null))
                        }

                        else -> {
                            clazz.getDeclaredMethod("run").invoke(clazz.getField("INSTANCE").get(null))
                        }
                    }
                }.onFailure {
                    pluginLogger.severe(it.stackTraceToString())
                }.onSuccess {
                    pluginLogger.info("Lighting up Neon......${loadingProgress}%")
                    Thread.sleep(150L)
                    return@forEachIndexed
                }
            }

            CoroutineScope(newSingleThreadContext("Neon Post-construction")).async {
                runCatching {
                    when {
                        (className.contains("Companion")) -> {
                            clazz.getDeclaredMethod("run").invoke(clazz.enclosingClass.getField("Companion").get(null))
                        }

                        else -> {
                            clazz.getDeclaredMethod("run").invoke(clazz.getField("INSTANCE").get(null))
                        }
                    }
                }.onFailure {
                    pluginLogger.severe(it.stackTraceToString())
                }.onSuccess {
                    pluginLogger.info("Lighting up Neon......${loadingProgress}%")
                    delay(150L)
                }
            }.asCompletableFuture().get()
        }

        pluginLogger.info("Neon has been lit up!")
        displayStartingTitle()
    }

    /**
     * Get simplified server version. E.g: 1.17, 1.18, 1.19 ...
     *
     * @return
     */
    fun getVersion(): String = version

    /**
     * Get raw server version. E.g: 1.17.1 1.18.2, 1.19.4
     *
     * @return
     */
    fun getRawVersion(): String = rawVersion

    /***
     * Register event processor. This is used when the server is starting up and only if the particular feature is enabled.
     */
    fun registerEventProcessor(eventProcessor: Listener) {
        HandlerList.getRegisteredListeners(plugin).forEach {
            if (it.listener.javaClass.canonicalName == eventProcessor.javaClass.canonicalName) return
        }

        plugin.server.pluginManager.registerEvents(eventProcessor, plugin)
    }

    /***
     * Unregister event. This is used when certain feature is disabled.
     */
    fun unRegisterEventProcessor(eventListener: Listener) {
        HandlerList.getRegisteredListeners(plugin).forEach {
            if (it.listener.javaClass.canonicalName != eventListener.javaClass.canonicalName) return@forEach

            HandlerList.unregisterAll(it.listener)
        }
    }

    private fun registerItemHighlight() {
        if (Enchantment.getByKey(NeonKeyGeneral.NGUI_HIGHTLIGHT_BUTTON.key) != null) return

        val nItemHighlight = NItemHighlight(NeonKeyGeneral.NGUI_HIGHTLIGHT_BUTTON.key)

        val field: Field = Enchantment::class.java.getDeclaredField("acceptingNew")
        field.isAccessible = true
        field.set(null, true)

        Enchantment.registerEnchantment(nItemHighlight)
    }

    private fun displayStartingTitle() {
        plugin.server.consoleSender.sendMessage(
            "${ChatColor.GOLD}|+++++++++++++++++=================+++++++++++++++++|"
        )
        plugin.server.consoleSender.sendMessage(
            "${ChatColor.GOLD}|-----------------=================-----------------|"
        )
        plugin.server.consoleSender.sendMessage(
            "${ChatColor.GOLD}$NEON_VERSION"
        )
        plugin.server.consoleSender.sendMessage(
            "${ChatColor.GOLD}|-----------------===${ChatColor.GREEN} <Started> ${ChatColor.GOLD}===-----------------|"
        )
        plugin.server.consoleSender.sendMessage(
            "${ChatColor.GOLD}|-----------------=================-----------------|"
        )
        plugin.server.consoleSender.sendMessage(
            "${ChatColor.GOLD}|+++++++++++++++++=================+++++++++++++++++|"
        )
    }

    fun displayClosingTitle() {
        plugin.server.consoleSender.sendMessage(
            "${ChatColor.GOLD}|+++++++++++++++++=================+++++++++++++++++|"
        )
        plugin.server.consoleSender.sendMessage(
            "${ChatColor.GOLD}|-----------------=================-----------------|"
        )
        plugin.server.consoleSender.sendMessage(
            "${ChatColor.GOLD}$NEON_VERSION"
        )
        plugin.server.consoleSender.sendMessage(
            "${ChatColor.GOLD}|-----------------===${ChatColor.RED} <Stopped> ${ChatColor.GOLD}===-----------------|"
        )
        plugin.server.consoleSender.sendMessage(
            "${ChatColor.GOLD}|-----------------=================-----------------|"
        )
        plugin.server.consoleSender.sendMessage(
            "${ChatColor.GOLD}|+++++++++++++++++=================+++++++++++++++++|"
        )
    }
}