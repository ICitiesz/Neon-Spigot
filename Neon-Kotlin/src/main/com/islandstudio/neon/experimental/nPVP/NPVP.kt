package com.islandstudio.neon.experimental.nPVP

import com.islandstudio.neon.experimental.nServerConfigurationNew.NServerConfigurationNew
import com.islandstudio.neon.stable.primary.nServerConfiguration.NServerConfiguration
import org.bukkit.Bukkit

object NPVP {
    private val serverWorlds = Bukkit.getServer().worlds

    fun run() {
        serverWorlds.forEach {
            if (NServerConfigurationNew.getToggle("nPVP")) {
                if (!it.pvp) {
                    it.pvp = true
                }
            } else {
                if (it.pvp) {
                    it.pvp = false
                }
            }
        }
    }
}