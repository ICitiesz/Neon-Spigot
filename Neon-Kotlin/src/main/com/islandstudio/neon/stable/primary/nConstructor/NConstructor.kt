package com.islandstudio.neon.stable.primary.nConstructor

import com.islandstudio.neon.Neon
import com.islandstudio.neon.experimental.nBundle.NBundle
import com.islandstudio.neon.experimental.nPVP.NPVP
import com.islandstudio.neon.experimental.nRepair.NRepair
import com.islandstudio.neon.stable.primary.nCommand.NCommand
import com.islandstudio.neon.stable.primary.nEvent.NEvent
import com.islandstudio.neon.stable.primary.nExperimental.NExperimental
import com.islandstudio.neon.stable.primary.nFolder.NFolder
import com.islandstudio.neon.stable.primary.nServerConfiguration.NServerConfiguration
import com.islandstudio.neon.stable.secondary.nCutter.NCutter
import com.islandstudio.neon.stable.secondary.nRank.NRank
import com.islandstudio.neon.stable.secondary.nSmelter.NSmelter
import com.islandstudio.neon.stable.secondary.nWaypoints.NWaypoints
import com.islandstudio.neon.stable.utils.NItemHighlight
import com.islandstudio.neon.stable.utils.NNamespaceKeys
import org.bukkit.ChatColor
import org.bukkit.enchantments.Enchantment
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin.getPlugin
import java.lang.reflect.Field

object NConstructor {
    private val plugin: Plugin = getPlugin(Neon::class.java)
    private val rawVersion: String = plugin.server.bukkitVersion.split("-")[0]
    private val version: String = rawVersion.split(".")[0] + "." + rawVersion.split(".")[1]

    fun constructPlugin() {
        when (version) {
            "1.17", "1.18" -> {
                plugin.server.consoleSender.sendMessage(
                    "${ChatColor.GRAY}[Neon] ${ChatColor.YELLOW}Detected Minecraft ${ChatColor.GREEN}$rawVersion${ChatColor.YELLOW}!"
                )

                Thread.sleep(1000)

                plugin.server.consoleSender.sendMessage(
                    "${ChatColor.GRAY}[Neon] ${ChatColor.YELLOW}Initializing features for Minecraft ${ChatColor.GREEN}$version${ChatColor.YELLOW}......"
                )

                /* Each initialization must be done according to priority. */
                /* Primary Component */
                plugin.server.pluginManager.registerEvents(NEvent(), plugin) // Event registration
                registerItemHighlight() // Item highlight registration

                /* Component will be arranged according to the priority */
                val nClasses = arrayOf(
                    NFolder::class.java,
                    NServerConfiguration.Handler::class.java,
                    NExperimental.Handler::class.java,
                    NCommand.Companion::class.java,
                    NRank::class.java,
                    NPVP::class.java,
                    NWaypoints.Handler::class.java,
                    NCutter::class.java,
                    NSmelter::class.java,
                    NBundle::class.java
                )

                /* Classes that not able to do async on the new thread */
                val notAsyncClassNames = arrayOf(
                    "NRank",
                    "NCutter",
                    "NSmelter",
                    "NBundle"
                )

                for (i in nClasses.indices) {
                    val progress: Int = ((i + 1).toFloat() / nClasses.size.toFloat() * 100).toInt()

                    val className: String = if (nClasses[i].simpleName == "Handler" || nClasses[i].simpleName == "Companion") {
                        val splitClassNames = nClasses[i].canonicalName.split(".")
                        "${splitClassNames[splitClassNames.size - 2]}.${splitClassNames[splitClassNames.size - 1]}"
                    } else {
                        nClasses[i].simpleName
                    }

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

//                NFolder.run()
//                NServerConfiguration.Handler.run()
//                NExperimental.Handler.run()
//                NCommand.run()
//                NRank.run()
//                NPVP.run()
//
//                /* Secondary Component */
//                NWaypoints.Handler.run()
//                NCutter.run()
//                NSmelter.run()
//                NRepair.run()

                plugin.server.consoleSender.sendMessage("${ChatColor.GRAY}[Neon] ${ChatColor.GREEN}Initialization complete!")
                sendIntro()
            }

            else -> {
                val supportedVersion = "1.17.X ~ 1.18.X"

                plugin.server.consoleSender.sendMessage(
                    "${ChatColor.GRAY}[Neon] ${ChatColor.RED}Incompatible Minecraft version! Please check for the latest version of Neon plugin!"
                )

                plugin.server.consoleSender.sendMessage(
                    "${ChatColor.GRAY}[Neon] ${ChatColor.YELLOW}Supported Minecraft version: ${ChatColor.GREEN}$supportedVersion"
                )

            }
        }
    }

    fun getVersion(): String {
        return version
    }

    private fun registerItemHighlight() {
        if (Enchantment.getByKey(NNamespaceKeys.NEON_BUTTON_HIGHLIGHT.key) != null) return

        val nItemHighlight = NItemHighlight(NNamespaceKeys.NEON_BUTTON_HIGHLIGHT.key)

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
            "${ChatColor.GOLD}|--------------== Neon v1.10-pre_2 ==---------------|"
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
            "${ChatColor.GOLD}|--------------== Neon v1.10-pre_2 ==---------------|"
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