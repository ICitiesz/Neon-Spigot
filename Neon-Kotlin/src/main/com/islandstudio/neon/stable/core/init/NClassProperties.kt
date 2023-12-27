package com.islandstudio.neon.stable.core.init

import com.islandstudio.neon.experimental.nFireworks.NFireworks
import com.islandstudio.neon.experimental.nPVP.NPVP
import com.islandstudio.neon.experimental.nPainting.NPainting
import com.islandstudio.neon.stable.core.event.NServerConstantProcessor
import com.islandstudio.neon.stable.core.io.nFolder.NFolder
import com.islandstudio.neon.stable.primary.nCommand.NCommand
import com.islandstudio.neon.stable.primary.nProfile.NProfile
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

data object NClassProperties {
    val preloadNClasses: ArrayList<Class<*>> = arrayListOf(
        NReflector.Handler::class.java,
        NServerFeatures.Handler::class.java,
        NFolder::class.java,
        NeonKey.Handler::class.java
    )

    val postloadClasses: ArrayList<Class<*>> = arrayListOf(
        NProfile.Handler::class.java,
        NServerConstantProcessor.Handler::class.java,
        NCommand.Companion::class.java,
        NRank.Handler::class.java,
        NPVP::class.java,
        NWaypoints.Handler::class.java,
        NDurable.Handler::class.java,
        NHarvest.Handler::class.java,
        NCutter.Handler::class.java,
        NSmelter.Handler::class.java,
        NBundle.Handler::class.java,
        NFireworks.Handler::class.java,
        NPainting.Handler::class.java
    )

    /* Classes that not able to do async on the new thread
    * If the run() method is in the nested class, the nClassName should include the nested class name
    * E.g: NDurable.Handler.run() | nClassName: NDurable.Handler
    */
    val syncClasses: ArrayList<Class<*>> = arrayListOf(
        NRank.Handler::class.java,
        NCutter.Handler::class.java,
        NSmelter.Handler::class.java,
        NBundle.Handler::class.java,
        NDurable.Handler::class.java
    )
}