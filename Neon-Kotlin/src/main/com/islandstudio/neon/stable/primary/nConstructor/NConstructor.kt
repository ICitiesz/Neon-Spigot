package com.islandstudio.neon.stable.primary.nConstructor

import com.islandstudio.neon.Neon
import com.islandstudio.neon.stable.primary.nEvent.NEvent
import com.islandstudio.neon.stable.utils.NItemHighlight
import com.islandstudio.neon.stable.utils.NeonKey
import org.bukkit.ChatColor
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin.getPlugin
import java.lang.reflect.Field

object NConstructor {
    val plugin: Plugin = getPlugin(Neon::class.java)

    private const val NEON_VERSION: String = "|-------------== Neon v1.10.1-pre_1 ==--------------|"
    private val rawVersion: String = plugin.server.bukkitVersion.split("-")[0]
    private val version: String = rawVersion.split(".")[0] + "." + rawVersion.split(".")[1]

    private val supportedVersion = listOf("1.17", "1.18", "1.19", "1.20")

    fun constructPlugin() {
        version.run {
            if (this in supportedVersion) {
                if ((rawVersion == "1.20" || rawVersion == "1.20.1")) return@run

                return@run
            }

            val supportedVersionRange = "1.17.X ~ 1.20.1"

            plugin.server.consoleSender.sendMessage(
                "${ChatColor.GRAY}[Neon] ${ChatColor.RED}Incompatible Minecraft version! Please check for the latest version of Neon plugin!"
            )

            plugin.server.consoleSender.sendMessage(
                "${ChatColor.GRAY}[Neon] ${ChatColor.YELLOW}Supported Minecraft version: ${ChatColor.GREEN}$supportedVersionRange"
            )

            return
        }

        plugin.server.consoleSender.sendMessage(
            "${ChatColor.GRAY}[Neon] ${ChatColor.YELLOW}Detected Minecraft ${ChatColor.GREEN}$rawVersion${ChatColor.YELLOW}!"
        )

        Thread.sleep(1000)

        plugin.server.consoleSender.sendMessage(
            "${ChatColor.GRAY}[Neon] ${ChatColor.YELLOW}Initializing features for Minecraft ${ChatColor.GREEN}$version${ChatColor.YELLOW}......"
        )

        /* Each initialization must be done according to priority. */
        /* Primary Component */
        registerEventProcessor(NEvent()) // Event registration
        registerItemHighlight() // Item highlight registration

        val nClasses = NClassProperties.NClasses.values().map { it.nClass }.toTypedArray()
        val notAsyncClassNames = NClassProperties.NotAsyncClassNames.values().map { it.nClassName }.toTypedArray()

        for (i in nClasses.indices) {
            val progress: Int = ((i + 1).toFloat() / nClasses.size.toFloat() * 100).toInt()

            /* Check if the simple name of the class is equal to "Handler" or "Companion",
            * if so, it split the canonical name and get the last 2 parts.
            * E.g.: com.islandstudio.neon.stable.primary.nCommand.NCommand.Companion -> NCommand.Companion
            */
            val className: String = if (nClasses[i].simpleName == "Handler" || nClasses[i].simpleName == "Companion") {
                val splitClassNames = nClasses[i].canonicalName.split(".")
                "${splitClassNames[splitClassNames.size - 2]}.${splitClassNames[splitClassNames.size - 1]}"
            } else {
                nClasses[i].simpleName
            }

            /* Check if the class is in the notAsyncClassNames array, if so,
            * the process will be done on the main thread.
            */
            if (className in notAsyncClassNames) {
                if (className.contains("Companion")) {
                    nClasses[i].getDeclaredMethod("run").invoke(nClasses[i].enclosingClass.getField("Companion").get(null))
                    plugin.server.consoleSender.sendMessage(
                        "${ChatColor.GRAY}[Neon] ${ChatColor.YELLOW}Initializing......${progress}%")
                    Thread.sleep(250)
                    continue
                }

                nClasses[i].getDeclaredMethod("run").invoke(nClasses[i].getField("INSTANCE").get(null))
                plugin.server.consoleSender.sendMessage(
                    "${ChatColor.GRAY}[Neon] ${ChatColor.YELLOW}Initializing......${progress}%")
                Thread.sleep(250)
                continue
            }

            /* If the class is not in the notAsyncClassNames array,
            * the process will be done on the new thread.
            */
            val thread = Thread {
                if (className.contains("Companion")) {
                    nClasses[i].getDeclaredMethod("run").invoke(nClasses[i].enclosingClass.getField("Companion").get(null))
                    return@Thread
                }

                nClasses[i].getDeclaredMethod("run").invoke(nClasses[i].getField("INSTANCE").get(null))
            }

            thread.name = "Server thread"
            thread.start()
            thread.join()

            plugin.server.consoleSender.sendMessage(
                "${ChatColor.GRAY}[Neon] ${ChatColor.YELLOW}Initializing......${progress}%")
            Thread.sleep(250)
        }

        plugin.server.consoleSender.sendMessage("${ChatColor.GRAY}[Neon] ${ChatColor.GREEN}Initialization complete!")
        sendIntro()
    }

    fun getVersion(): String = version

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
    fun unRegisterEvent(eventListener: Listener) {
        HandlerList.getRegisteredListeners(plugin).forEach {
            if (it.listener.javaClass.canonicalName != eventListener.javaClass.canonicalName) return@forEach

            HandlerList.unregisterAll(it.listener)
        }
    }

    private fun registerItemHighlight() {
        if (Enchantment.getByKey(NeonKey.NamespaceKeys.NEON_BUTTON_HIGHLIGHT.key) != null) return

        val nItemHighlight = NItemHighlight(NeonKey.NamespaceKeys.NEON_BUTTON_HIGHLIGHT.key)

        val field: Field = Enchantment::class.java.getDeclaredField("acceptingNew")
        field.isAccessible = true
        field.set(null, true)

        Enchantment.registerEnchantment(nItemHighlight)
    }

    private fun sendIntro() {
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

    fun sendOutro() {
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