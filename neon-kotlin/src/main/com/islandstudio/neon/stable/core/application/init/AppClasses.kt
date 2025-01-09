package com.islandstudio.neon.stable.core.application.init

import com.islandstudio.neon.experimental.nFireworks.NFireworks
import com.islandstudio.neon.experimental.nPVP.NPVP
import com.islandstudio.neon.experimental.nPainting.NPainting
import com.islandstudio.neon.stable.core.application.identity.NeonKey
import com.islandstudio.neon.stable.core.application.reflection.NmsProcessor
import com.islandstudio.neon.stable.core.command.NCommand
import com.islandstudio.neon.stable.core.event.ServerConstantEvent
import com.islandstudio.neon.stable.core.gui.NGUI
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
import java.lang.reflect.Method

enum class AppClasses(
    val clazz: Class<*>,
    val loadStage: LoadStage,
    /* Classes that not able to do async on the new thread
     * If the run() method is in the nested class, the nClassName should include the nested class name
     * E.g: NDurable.Handler.run() | nClassName: NDurable.Handler
     */
    val isSynchronous: Boolean, // TODO: Need change to canAsycn
    val isConfigReloadable: Boolean
) {
    /* #################################### Pre-init Classes #################################### */
    NmsProcessorClass(
        NmsProcessor.Companion::class.java,
        LoadStage.PreLoad,
        isSynchronous = false,
        isConfigReloadable = false
    ),

    NeonKeyClass(
        NeonKey.Handler::class.java,
        LoadStage.PreLoad,
        isSynchronous = false,
        isConfigReloadable =  false
    ),

//    DatabaseCacheManagerClass(
//        DatabaseCacheManager.Handler::class.java,
//        InitializationStage.PRE_INIT,
//        false,
//        false
//    ),

    NAccessPermissionClass(
        NAccessPermission.Handler::class.java,
        LoadStage.PreLoad,
        false,
        false
    ),

    NServerFeatureRemasteredClass(
        NServerFeaturesRemastered.Handler::class.java,
        LoadStage.PreLoad,
        false,
        false
    ),

    NServerFeatureClass(
        NServerFeatures.Handler::class.java,
        LoadStage.PreLoad,
        false,
        false
    ),

    NItemGlinterClass(
        NItemGlinter.Handler::class.java,
        LoadStage.PreLoad,
        false,
        false
    ),

    /* #################################### Post-init Classes #################################### */
    NProfileClass(
        NProfile.Handler::class.java,
        LoadStage.PostLoad,
        false,
        false
    ),

    NPlayerProfileClass(
        NPlayerProfile.Handler::class.java,
        LoadStage.PostLoad,
        false,
        false
    ),

    ServerConstantEventClass(
        ServerConstantEvent.Handler::class.java,
        LoadStage.PostLoad,
        false,
        false
    ),

    NCommandClass(
        NCommand.Companion::class.java,
        LoadStage.PostLoad,
        false,
        false
    ),

    NGUIClass(
        NGUI.Handler::class.java,
        LoadStage.PostLoad,
        false,
        false
    ),

    NRoleClass(
        NRole.Handler::class.java,
        LoadStage.PostLoad,
        false,
        false
    ),

    NRankClass(
        NRank.Handler::class.java,
        LoadStage.PostLoad,
        true,
        false
    ),

    NPVPClass(
        NPVP::class.java,
        LoadStage.PostLoad,
        false,
        true
    ),

    NWaypointsClass(
        NWaypoints.Handler::class.java,
        LoadStage.PostLoad,
        false,
        true
    ),

    NDurableClass(
        NDurable.Handler::class.java,
        LoadStage.PostLoad,
        true,
        true
    ),

    NHarvestClass(
        NHarvest.Handler::class.java,
        LoadStage.PostLoad,
        false,
        true
    ),

    NCutterClass(
        NCutter.Handler::class.java,
        LoadStage.PostLoad,
        true,
        true
    ),

    NSmelterClass(
        NSmelter.Handler::class.java,
        LoadStage.PostLoad,
        true,
        true
    ),

    NBundleClass(
        NBundle.Handler::class.java,
        LoadStage.PostLoad,
        true,
        true
    ),

    /* Experimental */
    NFireworksClass(
        NFireworks.Handler::class.java,
        LoadStage.PostLoad,
        false,
        true
    ),

    NPaintingClass(
        NPainting.Handler::class.java,
        LoadStage.PostLoad,
        false,
        true
    );

    companion object {
        const val CLASS_NAME_HANDLER = "Handler"
        const val CLASS_NAME_COMPANION = "Companion"
        const val FUNCTION_NAME_RUN = "run"
        const val FIELD_NAME_INSTANCE = "INSTANCE"

        fun getPreLoadClasses(): ArrayList<AppClasses> {
            return AppClasses.entries
                .filter {
                    it.loadStage == LoadStage.PreLoad
                }
                .toCollection(ArrayList())
        }

        fun getPostLoadClasses(): ArrayList<AppClasses> {
            return AppClasses.entries
                .filter {
                    it.loadStage == LoadStage.PostLoad
                }
                .toCollection(ArrayList())
        }

        fun invokeFunction(appClazz: AppClasses): Boolean {
            val clazz = appClazz.clazz

            /* Check if the simple name of the class is equal to "Handler" or "Companion",
            * if so, it split the canonical name and get the last 2 parts.
            * E.g.: com.islandstudio.neon.stable.primary.nCommand.NCommand.Companion -> NCommand.Companion
            */
            val clazzName = clazz.simpleName.takeIf { it != CLASS_NAME_HANDLER && it != CLASS_NAME_COMPANION }
                ?: clazz.canonicalName.split(".").let { "${it[it.size - 2]}.${it[it.size - 1]}" }

            val runFunction: Method = clazz.declaredMethods.find { method ->
                method.name == FUNCTION_NAME_RUN
            } ?: return false
            val invokeObject: Any

            if (clazzName.contains(CLASS_NAME_COMPANION)) {
                /* Perform invocation for `Companion` type class */
                invokeObject = clazz.enclosingClass.getField(CLASS_NAME_COMPANION).get(null)
            } else {
                /* Perform invocation for `Handler` type class */
                invokeObject = clazz.getField(FIELD_NAME_INSTANCE).get(null)
            }

            runFunction.invoke(invokeObject)
            return true
        }
    }
}