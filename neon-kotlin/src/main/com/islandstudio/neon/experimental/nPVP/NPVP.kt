package com.islandstudio.neon.experimental.nPVP

import com.islandstudio.neon.Neon
import com.islandstudio.neon.stable.core.application.di.IComponentInjector
import com.islandstudio.neon.stable.features.nServerFeatures.NServerFeaturesRemastered
import org.koin.core.component.inject
import kotlin.properties.Delegates

object NPVP: IComponentInjector {
    private val neon by inject<Neon>()
    private val serverWorlds = neon.server.worlds
    private var isEnabled by Delegates.notNull<Boolean>()

    /**
     * Initialization for nPVP
     *
     */
    fun run() {
        isEnabled = NServerFeaturesRemastered.serverFeatureSession.getActiveServerFeatureToggle("nPVP") ?: false

        serverWorlds.forEach {
            if (isEnabled) {
                if (!it.pvp) it.pvp = true

                return@forEach
            }

            if (it.pvp) it.pvp = false
        }
    }
}