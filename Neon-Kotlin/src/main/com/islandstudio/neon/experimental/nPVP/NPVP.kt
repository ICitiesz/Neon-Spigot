package com.islandstudio.neon.experimental.nPVP

import com.islandstudio.neon.stable.primary.nConstructor.NConstructor
import com.islandstudio.neon.stable.primary.nServerFeatures.NServerFeatures
import com.islandstudio.neon.stable.primary.nServerFeatures.ServerFeature

object NPVP {
    private val serverWorlds = NConstructor.plugin.server.worlds
    private var isEnabled = false

    /**
     * Initialization for nPVP
     *
     */
    fun run() {
        isEnabled = NServerFeatures.getToggle(ServerFeature.FeatureNames.N_PVP)

        serverWorlds.forEach {
            if (isEnabled) {
                if (!it.pvp) it.pvp = true

                return@forEach
            }

            if (it.pvp) it.pvp = false
        }
    }
}