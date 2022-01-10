package com.islandstudio.neon.stable.primary.nConstructor

import com.islandstudio.neon.Main
import com.islandstudio.neon.experimental.nPVP.NPVP
import com.islandstudio.neon.stable.secondary.nCutter.NCutter
import com.islandstudio.neon.stable.primary.nCommand.NCommand
import com.islandstudio.neon.stable.primary.nServerConfiguration.NServerConfiguration
import com.islandstudio.neon.stable.secondary.nSmelter.NSmelter
import com.islandstudio.neon.stable.secondary.nRank.NRank
import com.islandstudio.neon.stable.primary.nEvent.NEvent
import com.islandstudio.neon.stable.primary.nExperimental.NExperimental
import com.islandstudio.neon.stable.primary.nFolder.NFolder
import com.islandstudio.neon.stable.secondary.nWaypoints.NWaypoints
import com.islandstudio.neon.stable.utils.NItemHighlight
import com.islandstudio.neon.stable.utils.NNamespaceKeys
import org.bukkit.ChatColor
import org.bukkit.enchantments.Enchantment
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin.getPlugin
import java.lang.reflect.Field

object NConstructor {
    private val plugin: Plugin = getPlugin(Main::class.java)
    private val rawVersion: String = plugin.server.bukkitVersion.split("-")[0]
    private val version: String = rawVersion.split(".")[0] + "." + rawVersion.split(".")[1]

    fun constructPlugin() {
        when (version) {
            "1.17", "1.18" -> {
                plugin.server.consoleSender.sendMessage(
                    ChatColor.GRAY.toString() + "[Neon] " + ChatColor.YELLOW + "Detected Minecraft " + ChatColor.GREEN + rawVersion + ChatColor.YELLOW + "!"
                )

                Thread.sleep(1000)

                plugin.server.consoleSender.sendMessage(
                    ChatColor.GRAY.toString() + "[Neon] " + ChatColor.YELLOW + "Initializing features for Minecraft " + ChatColor.GREEN + version + ChatColor.YELLOW + "......"
                )

                /* Each initialization must be done according to priority. */
                /* Primary Component */
                plugin.server.pluginManager.registerEvents(NEvent(), plugin) // Event registration
                registerItemHighlight() // Item highlight registration
                NFolder.run()
                NServerConfiguration.Handler.run()
                NExperimental.Handler.run()
                NCommand.run()
                NRank.run()
                NPVP.run()

                /* Secondary Component */
                NWaypoints.Handler.run()
                NCutter.run()
                NSmelter.run()

                Thread.sleep(2500)
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
            "${ChatColor.GOLD}|--------------== Neon v1.10-pre_1 ==---------------|"
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
            "${ChatColor.GOLD}|--------------== Neon v1.10-pre_1 ==---------------|"
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