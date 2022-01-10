package com.islandstudio.neon.experimental.nPVP

import com.islandstudio.neon.stable.primary.nServerConfiguration.NServerConfiguration
import org.bukkit.Bukkit

object NPVP {
    private val serverWorlds = Bukkit.getServer().worlds

    fun run() {
        serverWorlds.forEach {
            if (NServerConfiguration.Handler.getServerConfig()["nPVP"] == true) {
                if (!it.pvp) {
                    it.pvp = true
                }
            } else if (NServerConfiguration.Handler.getServerConfig()["nPVP"] == false) {
                if (it.pvp) {
                    it.pvp = false
                }
            }
        }
    }
}