package com.islandstudio.neon.core.initialization

import com.islandstudio.neon.command.CommandManager
import com.islandstudio.neon.core.datakey.DataKeyManager
import com.islandstudio.neon.core.nmsmapping.NmsManager
import com.islandstudio.neon.core.nmsmapping.NmsProcessor
import com.islandstudio.neon.experimental.gui.GuiManager
import com.islandstudio.neon.experimental.nPVP.NPVP
import com.islandstudio.neon.features.durabilityplus.DurabilityPlus
import com.islandstudio.neon.features.nBundle.NBundle
import com.islandstudio.neon.features.nCutter.NCutter
import com.islandstudio.neon.features.nHarvest.NHarvest
import com.islandstudio.neon.features.nSmelter.NSmelter
import com.islandstudio.neon.features.neonfeature.NeonFeatureManager
import com.islandstudio.neon.item.EnchantmentManager
import com.islandstudio.neon.player.security.AccessControlManager
import com.islandstudio.neon.player.security.role.RoleManager
import com.islandstudio.neon.player.session.PlayerSessionManager
import java.lang.reflect.Method

enum class NeonPluginClasses(
    val clazz: Class<*>,
    val loadStage: LoadStage,
    /* Classes that not able to do async on the new thread
     * If the run() method is in the nested class, the nClassName should include the nested class name
     * E.g: NDurable.Handler.run() | nClassName: NDurable.Handler
     */
    val canAsync: Boolean, // Commit b5b6607 - Previously is `isSynchronous`, if change to `canAsync`, those data previously was true should be false now
    val isConfigReloadable: Boolean
) {
    /* #################################### Pre-init Classes #################################### */
    NmsManagerClass(
        NmsManager::class.java,
        LoadStage.PreLoad,
        true,
        false
    ),

    NmsProcessorClass(
        NmsProcessor.Companion::class.java,
        LoadStage.PreLoad,
        true,
        false
    ),

    DataKeyManagerClass(
      DataKeyManager.Companion::class.java,
        LoadStage.PreLoad,
        true,
        false
    ),

//    NeonKeyClass(
//        NeonKey.Handler::class.java,
//        LoadStage.PreLoad,
//        true,
//        false
//    ),

//    DatabaseCacheManagerClass(
//        DatabaseCacheManager.Handler::class.java,
//        InitializationStage.PRE_INIT,
//        false,
//        false
//    ),

    AccessControlManagerClass(
      AccessControlManager.Companion::class.java,
        LoadStage.PostLoad,
        true,
        false
    ),

    NeonFeatureManagerClass(
        NeonFeatureManager.Companion::class.java,
        LoadStage.PreLoad,
        true,
        false
    ),

    EnchantmentManagerClass(
        EnchantmentManager.Companion::class.java,
        LoadStage.PreLoad,
        true,
        false
    ),

//    NItemGlinterClass(
//        NItemGlinter.Handler::class.java,
//        LoadStage.PreLoad,
//        true,
//        false
//    ),

    /* #################################### Post-init Classes #################################### */
    PlayerSessionManagerClass(
        PlayerSessionManager.Companion::class.java,
        LoadStage.PostLoad,
        true,
        false
    ),

//    NProfileClass(
//        NProfile.Handler::class.java,
//        LoadStage.PostLoad,
//        true,
//        false
//    ),

//    NPlayerProfileClass(
//        NPlayerProfile.Handler::class.java,
//        LoadStage.PostLoad,
//        true,
//        false
//    ),

//    ServerConstantEventClass(
//        ServerConstantEvent.Handler::class.java,
//        LoadStage.PostLoad,
//        true,
//        false
//    ),

//    NCommandClass(
//        NCommand.Companion::class.java,
//        LoadStage.PostLoad,
//        false,
//        false
//    ),

    CommandManagerClass(
        CommandManager.Companion::class.java,
        LoadStage.PostLoad,
        true,
        false
    ),

//    NGUIClass(
//        NGUI.Handler::class.java,
//        LoadStage.PostLoad,
//        false,
//        false
//    ),

    GuiManagerClass(
        GuiManager.Companion::class.java,
        LoadStage.PostLoad,
        true,
        false
    ),

    RoleManagerClass(
        RoleManager.Companion::class.java,
        LoadStage.PostLoad,
        false,
        false
    ),

//    NRankClass(
//        NRank.Handler::class.java,
//        LoadStage.PostLoad,
//        false,
//        false
//    ),

    NPVPClass(
        NPVP::class.java,
        LoadStage.PostLoad,
        true,
        true
    ),

//    NWaypointsClass(
//        NWaypoints.Handler::class.java,
//        LoadStage.PostLoad,
//        true,
//        true
//    ),

//    NDurableClass(
//        NDurable.Handler::class.java,
//        LoadStage.PostLoad,
//        false,
//        true
//    ),

    DurabilityPlusClass(
        DurabilityPlus.Companion::class.java,
        LoadStage.PostLoad,
        false,
        true
    ),

    NHarvestClass(
        NHarvest.Handler::class.java,
        LoadStage.PostLoad,
        true,
        true
    ),

    NCutterClass(
        NCutter.Companion::class.java,
        LoadStage.PostLoad,
        false,
        true
    ),

    NSmelterClass(
        NSmelter.Handler::class.java,
        LoadStage.PostLoad,
        false,
        true
    ),

    NBundleClass(
        NBundle.Handler::class.java,
        LoadStage.PostLoad,
        false,
        true
    ),

    /* Experimental */
/*    NFireworksClass(
        NFireworks.Handler::class.java,
        LoadStage.PostLoad,
        true,
        true
    ),

    NPaintingClass(
        NPainting.Handler::class.java,
        LoadStage.PostLoad,
        true,
        true
    )*/;

    companion object {
        const val CLASS_NAME_HANDLER = "Handler"
        const val CLASS_NAME_COMPANION = "Companion"
        const val FUNCTION_NAME_RUN = "run"
        const val FIELD_NAME_INSTANCE = "INSTANCE"

        fun getPreLoadClasses(): ArrayList<NeonPluginClasses> {
            return NeonPluginClasses.entries
                .filter {
                    it.loadStage == LoadStage.PreLoad
                }
                .toCollection(ArrayList())
        }

        fun getPostLoadClasses(): ArrayList<NeonPluginClasses> {
            return NeonPluginClasses.entries
                .filter {
                    it.loadStage == LoadStage.PostLoad
                }
                .toCollection(ArrayList())
        }

        fun invokeFunction(neonPluginClass: NeonPluginClasses): Boolean {
            val clazz = neonPluginClass.clazz

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