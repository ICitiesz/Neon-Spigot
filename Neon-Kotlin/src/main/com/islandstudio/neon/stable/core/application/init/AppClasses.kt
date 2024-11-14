package com.islandstudio.neon.stable.core.application.init

import com.islandstudio.neon.experimental.nFireworks.NFireworks
import com.islandstudio.neon.experimental.nPVP.NPVP
import com.islandstudio.neon.experimental.nPainting.NPainting
import com.islandstudio.neon.stable.core.application.identifier.NeonKey
import com.islandstudio.neon.stable.core.application.reflection.NReflector
import com.islandstudio.neon.stable.core.application.reflection.remastered.NmsProcessor
import com.islandstudio.neon.stable.core.command.NCommand
import com.islandstudio.neon.stable.core.database.DatabaseCacheManager
import com.islandstudio.neon.stable.core.database.DatabaseConnector
import com.islandstudio.neon.stable.core.event.ServerConstantEvent
import com.islandstudio.neon.stable.core.gui.NGUI
import com.islandstudio.neon.stable.core.io.nFile.NFile
import com.islandstudio.neon.stable.features.nBundle.NBundle
import com.islandstudio.neon.stable.features.nCutter.NCutter
import com.islandstudio.neon.stable.features.nDurable.NDurable
import com.islandstudio.neon.stable.features.nHarvest.NHarvest
import com.islandstudio.neon.stable.features.nRank.NRank
import com.islandstudio.neon.stable.features.nServerFeatures.NServerFeaturesRemastered
import com.islandstudio.neon.stable.features.nSmelter.NSmelter
import com.islandstudio.neon.stable.features.nWaypoints.NWaypoints
import com.islandstudio.neon.stable.item.NItemGlinter
import com.islandstudio.neon.stable.player.NPlayerProfile
import com.islandstudio.neon.stable.player.nAccessPermission.NAccessPermission
import com.islandstudio.neon.stable.player.nRole.NRole
import com.islandstudio.neon.stable.primary.nProfile.NProfile
import com.islandstudio.neon.stable.primary.nServerFeatures.NServerFeatures

enum class AppClasses(
    val clazz: Class<*>,
    val initializationStage: InitializationStage,
    /** Classes that not able to do async on the new thread
     * If the run() method is in the nested class, the nClassName should include the nested class name
     * E.g: NDurable.Handler.run() | nClassName: NDurable.Handler
     */
    val isSynchronous: Boolean,
    val isConfigReloadable: Boolean
) {
    /* #################################### Pre-init Classes #################################### */
    NmsProcessorClass(
        NmsProcessor.Companion::class.java,
        InitializationStage.PRE_INIT,
        isSynchronous = false,
        isConfigReloadable = false
    ),

    NReflectorClass(
        NReflector.Handler::class.java,
        InitializationStage.PRE_INIT,
        false,
        false
    ),

    NFileClass(
        NFile::class.java,
        InitializationStage.PRE_INIT,
        false,
        false
    ),

    NeonKeyClass(
        NeonKey.Handler::class.java,
        InitializationStage.PRE_INIT,
        false,
        false
    ),

    DatabaseCacheManagerClass(
        DatabaseCacheManager.Handler::class.java,
        InitializationStage.PRE_INIT,
        false,
        false
    ),

    DatabaseConnectorClass(
        DatabaseConnector::class.java,
        InitializationStage.PRE_INIT,
        false,
        false
    ),

    NAccessPermissionClass(
        NAccessPermission.Handler::class.java,
        InitializationStage.PRE_INIT,
        false,
        false
    ),

    NServerFeatureRemasteredClass(
        NServerFeaturesRemastered.Handler::class.java,
        InitializationStage.PRE_INIT,
        false,
        false
    ),

    NServerFeatureClass(
        NServerFeatures.Handler::class.java,
        InitializationStage.PRE_INIT,
        false,
        false
    ),

    NItemGlinterClass(
        NItemGlinter.Handler::class.java,
        InitializationStage.PRE_INIT,
        false,
        false
    ),

    /* #################################### Post-init Classes #################################### */
    NProfileClass(
        NProfile.Handler::class.java,
        InitializationStage.POST_INIT,
        false,
        false
    ),

    NPlayerProfileClass(
        NPlayerProfile.Handler::class.java,
        InitializationStage.POST_INIT,
        false,
        false
    ),

    ServerConstantEventClass(
        ServerConstantEvent.Handler::class.java,
        InitializationStage.POST_INIT,
        false,
        false
    ),

    NCommandClass(
        NCommand.Companion::class.java,
        InitializationStage.POST_INIT,
        false,
        false
    ),

    NGUIClass(
        NGUI.Handler::class.java,
        InitializationStage.POST_INIT,
        false,
        false
    ),

    NRoleClass(
        NRole.Handler::class.java,
        InitializationStage.POST_INIT,
        false,
        false
    ),

    NRankClass(
        NRank.Handler::class.java,
        InitializationStage.POST_INIT,
        true,
        false
    ),

    NPVPClass(
        NPVP::class.java,
        InitializationStage.POST_INIT,
        false,
        true
    ),

    NWaypointsClass(
        NWaypoints.Handler::class.java,
        InitializationStage.POST_INIT,
        false,
        true
    ),

    NDurableClass(
        NDurable.Handler::class.java,
        InitializationStage.POST_INIT,
        true,
        true
    ),

    NHarvestClass(
        NHarvest.Handler::class.java,
        InitializationStage.POST_INIT,
        false,
        true
    ),

    NCutterClass(
        NCutter.Handler::class.java,
        InitializationStage.POST_INIT,
        true,
        true
    ),

    NSmelterClass(
        NSmelter.Handler::class.java,
        InitializationStage.POST_INIT,
        true,
        true
    ),

    NBundleClass(
        NBundle.Handler::class.java,
        InitializationStage.POST_INIT,
        true,
        true
    ),

    /* Experimental */
    NFireworksClass(
        NFireworks.Handler::class.java,
        InitializationStage.POST_INIT,
        false,
        true
    ),

    NPaintingClass(
        NPainting.Handler::class.java,
        InitializationStage.POST_INIT,
        false,
        true
    )
}