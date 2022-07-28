package com.islandstudio.neon.stable.primary.nConstructor

import com.islandstudio.neon.experimental.nBundle.NBundle
import com.islandstudio.neon.experimental.nDurable.NDurable
import com.islandstudio.neon.experimental.nPVP.NPVP
import com.islandstudio.neon.experimental.nServerFeaturesBeta.NServerFeatures
import com.islandstudio.neon.stable.primary.nCommand.NCommand
import com.islandstudio.neon.stable.primary.nExperimental.NExperimental
import com.islandstudio.neon.stable.primary.nFolder.NFolder
import com.islandstudio.neon.stable.primary.nServerConfiguration.NServerConfiguration
import com.islandstudio.neon.stable.secondary.nCutter.NCutter
import com.islandstudio.neon.stable.secondary.nHarvest.NHarvest
import com.islandstudio.neon.stable.secondary.nRank.NRank
import com.islandstudio.neon.stable.secondary.nSmelter.NSmelter
import com.islandstudio.neon.stable.secondary.nWaypoints.NWaypoints

object NClassProperties {
    enum class NClasses(val nClass: Class<*>) {
        /* Classes will be arranged according to the priority */
        N_FOLDER(NFolder::class.java),
        N_SERVER_CONFIGURATION_NEW(NServerFeatures.Handler::class.java),
        N_SERVER_CONFIGURATION(NServerConfiguration.Handler::class.java),
        N_EXPERIMENTAL_HANDLER(NExperimental.Handler::class.java),
        N_COMMAND_COMPANION(NCommand.Companion::class.java),
        N_RANK(NRank::class.java),
        N_PVP(NPVP::class.java),
        N_WAYPOINTS_HANDLER(NWaypoints.Handler::class.java),
        N_DURABLE_HANDLER(NDurable.Handler::class.java),
        N_HARVEST(NHarvest::class.java),
        N_CUTTER(NCutter::class.java),
        N_SMELTER(NSmelter::class.java),
        N_BUNDLE(NBundle::class.java)
    }

    enum class NotAsyncClassNames(val nClassName: String) {
        /* Classes that not able to do async on the new thread */
        N_RANK("NRank"),
        N_CUTTER("NCutter"),
        N_SMELTER("NSmelter"),
        N_BUNDLE("NBundle")
    }
}