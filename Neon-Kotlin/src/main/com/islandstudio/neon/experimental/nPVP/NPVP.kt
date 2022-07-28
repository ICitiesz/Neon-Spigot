package com.islandstudio.neon.experimental.nPVP

import com.islandstudio.neon.experimental.nServerFeaturesBeta.NServerFeatures
import org.bukkit.Bukkit

object NPVP {
    private val serverWorlds = Bukkit.getServer().worlds

    fun run() {
        serverWorlds.forEach {
            if (NServerFeatures.getToggle("nPVP")) {
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