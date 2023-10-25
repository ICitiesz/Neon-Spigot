package com.islandstudio.neon.stable.primary.nConstructor

import com.islandstudio.neon.experimental.nFireworks.NFireworks
import com.islandstudio.neon.experimental.nPVP.NPVP
import com.islandstudio.neon.stable.core.io.nFolder.NFolder
import com.islandstudio.neon.stable.primary.nCommand.NCommand
import com.islandstudio.neon.stable.primary.nProfile.NProfile
import com.islandstudio.neon.stable.primary.nServerConstantEventProcessor.NServerConstantProcessor
import com.islandstudio.neon.stable.primary.nServerFeatures.NServerFeatures
import com.islandstudio.neon.stable.secondary.nBundle.NBundle
import com.islandstudio.neon.stable.secondary.nCutter.NCutter
import com.islandstudio.neon.stable.secondary.nDurable.NDurable
import com.islandstudio.neon.stable.secondary.nHarvest.NHarvest
import com.islandstudio.neon.stable.secondary.nRank.NRank
import com.islandstudio.neon.stable.secondary.nSmelter.NSmelter
import com.islandstudio.neon.stable.secondary.nWaypoints.NWaypoints
import com.islandstudio.neon.stable.utils.NeonKey
import com.islandstudio.neon.stable.utils.reflection.NReflector

object NClassProperties {
    enum class NClasses(val nClass: Class<*>) {
        /* Classes will be arranged according to the priority */
        N_FOLDER(NFolder::class.java),
        N_SERVER_FEATURES(NServerFeatures.Handler::class.java),
        NEON_KEY(NeonKey.Handler::class.java),
        N_REFLECTOR(NReflector.Handler::class.java),
        N_PROFILE(NProfile.Handler::class.java),
        N_SERVER_CONSTANT_PROCESSOR_HANDLER(NServerConstantProcessor.Handler::class.java),
        N_COMMAND_COMPANION(NCommand.Companion::class.java),
        N_RANK_HANDLER(NRank.Handler::class.java),
        N_PVP(NPVP::class.java),
        N_WAYPOINTS_HANDLER(NWaypoints.Handler::class.java),
        N_DURABLE_HANDLER(NDurable.Handler::class.java),
        N_HARVEST_HANDLER(NHarvest.Handler::class.java),
        N_CUTTER_HANDLER(NCutter.Handler::class.java),
        N_SMELTER_HANDLER(NSmelter.Handler::class.java),
        N_BUNDLE_HANDLER(NBundle.Handler::class.java),
        N_FIREWORKS(NFireworks.Handler::class.java)
    }

    enum class NotAsyncClassNames(val nClassName: String) {
        /* Classes that not able to do async on the new thread
        * If the run() method is in the nested class, the nClassName should include the nested class name
        * E.g: NDurable.Handler.run() | nClassName: NDurable.Handler
        * */
        N_RANK("NRank.Handler"),
        N_CUTTER("NCutter.Handler"),
        N_SMELTER("NSmelter.Handler"),
        N_BUNDLE("NBundle.Handler"),
        N_DURABLE("NDurable.Handler")
    }
}