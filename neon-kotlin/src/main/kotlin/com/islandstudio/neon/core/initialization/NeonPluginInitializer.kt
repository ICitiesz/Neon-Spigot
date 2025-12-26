package com.islandstudio.neon.core.initialization

import com.islandstudio.neon.apinew.connection.NeonDatabaseManager
import com.islandstudio.neon.core.datakey.DataKeyManager
import com.islandstudio.neon.core.di.module.NeonModule
import com.islandstudio.neon.core.nmsmapping.NmsManagerNew
import com.islandstudio.neon.player.permission.PermissionManager
import com.islandstudio.neon.player.role.RoleManagerNew
import com.islandstudio.neon.player.session.PlayerSessionManagerNew
import com.islandstudio.neon.shared.core.di.IComponentProvider
import com.islandstudio.neon.shared.core.di.PluginDIManager
import com.islandstudio.neon.shared.core.di.SharedModule
import com.islandstudio.neon.shared.core.di.getComponent
import com.islandstudio.neon.shared.core.initialization.IPluginInitializer
import com.islandstudio.neon.shared.core.initialization.PluginContext
import com.islandstudio.neon.shared.experimental.utils.coroutines.CloseableCoroutineScope
import com.islandstudio.neon.util.NeonColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import org.bukkit.Bukkit
import org.koin.ksp.generated.module

class NeonPluginInitializer(private val pluginContext: PluginContext): IPluginInitializer, NmsManagerNew.INmsMapper, IComponentProvider {
    private val neonVersionText = "${NeonColor.DefinedColor.CyanBlue.color}${NeonColor.DefinedColor.Bold.color}v${pluginContext.mainPluginInstance.description.version}${NeonColor.DefinedColor.Reset.color}"
    //private val neon = DataUtil.asType<Neon>(pluginContext.mainPluginInstance)
    private val initCloseableCoroutineScope = CloseableCoroutineScope(Dispatchers.IO)


    /*
        *        _____                                                      _____
                {_____}                                                    {_____}
                 | ~ |~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~| ~ |
                 | ~ |  +================================================+  | ~ |
                 | ~ |  ░████    ░███  ░████████    ░████    ░████    ░███  | ~ |
                 | ~ |  ░███░███ ░███  ░███      ░███   ░███ ░███░███ ░███  | ~ |
                 | ~ |  ░███ ░███░███  ░███████  ░███   ░███ ░███ ░███░███  | ~ |
                 | ~ |  ░███   ░█████  ░███      ░███   ░███ ░███   ░█████  | ~ |
                 | ~ |  ░███    ░████  ░████████    ░████    ░███    ░████  | ~ |
                 | ~ |  +================================================+  | ~ |
                 |___|~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~|___|
                {_____}              ()                   ()               {_____}
                                      ++=================++
                                      ||   ~ STARTED ~   ||
                                      || ~ v1.11-pre_1 ~ ||
                                      ++=================++

        *
        * */

    private val NEON_ON_ENABLED_TITLE = "\n" + """
                 ${NeonColor.DefinedColor.Yellow.color}_____                                                                _____
                ${NeonColor.DefinedColor.Yellow.color}{_____}                                                              {_____}
                 ${NeonColor.DefinedColor.Purple.color}| ~ |${NeonColor.DefinedColor.Orange.color}~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~${NeonColor.DefinedColor.Purple.color}| ~ |
                 ${NeonColor.DefinedColor.Purple.color}| ~ |  ${NeonColor.DefinedColor.LightGreen.color}+==========================================================+  ${NeonColor.DefinedColor.Purple.color}| ~ |
                 ${NeonColor.DefinedColor.Purple.color}| ~ |          ${NeonColor.DefinedColor.CyanBlue.color}░███    ░██ ░███████    ░███    ░███    ░██           ${NeonColor.DefinedColor.Purple.color}| ~ |
                 ${NeonColor.DefinedColor.Purple.color}| ~ |          ${NeonColor.DefinedColor.CyanBlue.color}░████   ░██ ░██       ░██  ░██  ░████   ░██           ${NeonColor.DefinedColor.Purple.color}| ~ |
                 ${NeonColor.DefinedColor.Purple.color}| ~ |          ${NeonColor.DefinedColor.CyanBlue.color}░██ ░██ ░██ ░██████  ░██    ░██ ░██ ░██ ░██           ${NeonColor.DefinedColor.Purple.color}| ~ |
                 ${NeonColor.DefinedColor.Purple.color}| ~ |          ${NeonColor.DefinedColor.CyanBlue.color}░██   ░████ ░██       ░██  ░██  ░██   ░████           ${NeonColor.DefinedColor.Purple.color}| ~ |
                 ${NeonColor.DefinedColor.Purple.color}| ~ |          ${NeonColor.DefinedColor.CyanBlue.color}░██    ░███ ░███████    ░███    ░██    ░███           ${NeonColor.DefinedColor.Purple.color}| ~ |
                 ${NeonColor.DefinedColor.Purple.color}| ~ |  ${NeonColor.DefinedColor.LightGreen.color}+==========================================================+  ${NeonColor.DefinedColor.Purple.color}| ~ |
                 ${NeonColor.DefinedColor.Purple.color}|___|${NeonColor.DefinedColor.Orange.color}~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~${NeonColor.DefinedColor.Purple.color}|___|
                ${NeonColor.DefinedColor.Yellow.color}{_____}${NeonColor.DefinedColor.Reset.color}                  ()                     ()                   ${NeonColor.DefinedColor.Yellow.color}{_____}${NeonColor.DefinedColor.Reset.color}
                                         ++=====================++
                                         ||     ~ ${NeonColor.DefinedColor.Green.color}${NeonColor.DefinedColor.Bold.color}STARTED${NeonColor.DefinedColor.Reset.color} ~     ||
                                         ||   ~ $neonVersionText ~   ||
                                         ++=====================++
        """.trimIndent() + "\n"

    private val NEON_ON_DISABLED_TITLE = "\n" + """
                 ${NeonColor.DefinedColor.Yellow.color}_____                                                                _____
                ${NeonColor.DefinedColor.Yellow.color}{_____}                                                              {_____}
                 ${NeonColor.DefinedColor.Purple.color}| ~ |${NeonColor.DefinedColor.Orange.color}~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~${NeonColor.DefinedColor.Purple.color}| ~ |
                 ${NeonColor.DefinedColor.Purple.color}| ~ |  ${NeonColor.DefinedColor.LightGreen.color}+==========================================================+  ${NeonColor.DefinedColor.Purple.color}| ~ |
                 ${NeonColor.DefinedColor.Purple.color}| ~ |          ${NeonColor.DefinedColor.CyanBlue.color}░███    ░██ ░███████    ░███    ░███    ░██           ${NeonColor.DefinedColor.Purple.color}| ~ |
                 ${NeonColor.DefinedColor.Purple.color}| ~ |          ${NeonColor.DefinedColor.CyanBlue.color}░████   ░██ ░██       ░██  ░██  ░████   ░██           ${NeonColor.DefinedColor.Purple.color}| ~ |
                 ${NeonColor.DefinedColor.Purple.color}| ~ |          ${NeonColor.DefinedColor.CyanBlue.color}░██ ░██ ░██ ░██████  ░██    ░██ ░██ ░██ ░██           ${NeonColor.DefinedColor.Purple.color}| ~ |
                 ${NeonColor.DefinedColor.Purple.color}| ~ |          ${NeonColor.DefinedColor.CyanBlue.color}░██   ░████ ░██       ░██  ░██  ░██   ░████           ${NeonColor.DefinedColor.Purple.color}| ~ |
                 ${NeonColor.DefinedColor.Purple.color}| ~ |          ${NeonColor.DefinedColor.CyanBlue.color}░██    ░███ ░███████    ░███    ░██    ░███           ${NeonColor.DefinedColor.Purple.color}| ~ |
                 ${NeonColor.DefinedColor.Purple.color}| ~ |  ${NeonColor.DefinedColor.LightGreen.color}+==========================================================+  ${NeonColor.DefinedColor.Purple.color}| ~ |
                 ${NeonColor.DefinedColor.Purple.color}|___|${NeonColor.DefinedColor.Orange.color}~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~${NeonColor.DefinedColor.Purple.color}|___|
                ${NeonColor.DefinedColor.Yellow.color}{_____}${NeonColor.DefinedColor.Reset.color}                  ()                     ()                   ${NeonColor.DefinedColor.Yellow.color}{_____}${NeonColor.DefinedColor.Reset.color}
                                         ++=====================++
                                         ||     ~ ${NeonColor.DefinedColor.Red.color}${NeonColor.DefinedColor.Bold.color}DISABLED${NeonColor.DefinedColor.Reset.color} ~    ||
                                         ||   ~ $neonVersionText ~   ||
                                         ++=====================++
        """.trimIndent() + "\n"

    init {
        PluginDIManager.startPluginScoped(
            pluginContext,
            NeonModule().module,
            SharedModule().module
        )
    }

    private val neonDatabaseManager = NeonDatabaseManager()
    private var onLoadJob: Job? = null

    override fun onLoad() {
        onLoadJob = initCloseableCoroutineScope.launchJob {
            initCloseableCoroutineScope.launchAsCompletableDeferred {
                async {
                    pluginContext.loadCodeMessages()
                }.await()

                async {
                    getKoin().declare(neonDatabaseManager)
                    neonDatabaseManager.initialize()
                }.await()

                async {
                    NmsManagerNew.runSuspend()
                }.await()

                async {
                    PermissionManager().runSuspend()
                }.await()

                async {
                    DataKeyManager.runSuspend()
                }.await()
            }.await()
        }
    }

    override fun onEnable() {
        onLoadJob?.invokeOnCompletion {
            PlayerSessionManagerNew().run()

            Bukkit.getScheduler().runTask(pluginContext.mainPluginInstance, Runnable {
                RoleManagerNew().run()
            })

            val permissionManager = getComponent<PermissionManager>()

//            CloseableCoroutineScope(Dispatchers.IO).launchJob {
//
//            }



            pluginContext.getServer().consoleSender.sendMessage(NEON_ON_ENABLED_TITLE)
        }
    }

    override fun onDisable() {
        neonDatabaseManager.close()
        pluginContext.getServer().consoleSender.sendMessage(NEON_ON_DISABLED_TITLE)
    }
}