package com.islandstudio.neon.rework.core.initialization

import com.islandstudio.neon.Neon
import com.islandstudio.neon.core.di.module.NeonModule
import com.islandstudio.neon.persistence.DatabaseManager
import com.islandstudio.neon.rework.core.datakey.DataKeyManager
import com.islandstudio.neon.rework.core.nms.NmsManagerRework
import com.islandstudio.neon.shared.experimental.utils.coroutines.CloseableCoroutineScope
import com.islandstudio.neon.shared.rework.core.di.IComponentProvider
import com.islandstudio.neon.shared.rework.core.di.PluginDIManager
import com.islandstudio.neon.shared.rework.core.di.getKoinContext
import com.islandstudio.neon.shared.rework.core.initialization.IPluginInitializer
import com.islandstudio.neon.shared.rework.core.initialization.NeonClassLoader
import com.islandstudio.neon.shared.rework.core.initialization.context.PluginContext
import com.islandstudio.neon.shared.utils.data.DataUtil
import com.islandstudio.neon.util.NeonColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.bukkit.plugin.Plugin
import org.koin.ksp.generated.module
import java.io.File
import java.lang.reflect.Proxy

class PluginInitializer(private val pluginContext: PluginContext): IPluginInitializer, IComponentProvider {
    companion object {
        private val INITIALIZER_CLASS_PATH = this.javaClass.enclosingClass.name

        fun load(neon: Neon, neonPluginFile: File, neonClassLoader: NeonClassLoader): IPluginInitializer {
            neon.logger.info("Loading plugin initializer......")

            val pluginScopedPluginContext = neonClassLoader.loadClass(PluginContext::class.java.name, true).run {
                this.getDeclaredConstructor(Plugin::class.java, File::class.java).newInstance(neon, neonPluginFile)
            }

            val pluginInitializerProxy = neonClassLoader.loadClass(INITIALIZER_CLASS_PATH).declaredConstructors.first().newInstance(pluginScopedPluginContext).run {
                Proxy.newProxyInstance(IPluginInitializer::class.java.classLoader, arrayOf(IPluginInitializer::class.java)) { _, method, args ->
                    /* Resolve equivalent method on child instance’s class */
                    val childMethod = this.javaClass.getMethod(method.name,
                        *method.parameterTypes.map { type ->
                            /* Remap parameter types into neon classloader if needed */
                            try { Class.forName(type.name, false, this.javaClass.classLoader) }
                            catch (_: ClassNotFoundException) { type }
                        }.toTypedArray()
                    )

                    childMethod.invoke(this, *(args ?: emptyArray()))
                }
            }

            return DataUtil.asType<IPluginInitializer>(pluginInitializerProxy)
        }
    }

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
        PluginDIManager.start(
            pluginContext,
            NeonModule().module,
        )
    }

    private val databaseManager = DatabaseManager(pluginContext)
    private var onLoadJob: Job? = null

    override fun onLoad() {
        onLoadJob = initCloseableCoroutineScope.launchJob {
            initCloseableCoroutineScope.launchAsCompletableDeferred {
                /* Stage 1: Load code message */
                pluginContext.loadCodeMessages()

                /* Stage 2: Initialize database manager */
                val databaseManagerInitJob = async {
                    getKoinContext().declare(databaseManager)
                    databaseManager.runSuspend()
                }

                /* Stage 3: Initialize NMS manager */
                val nmsManagerInitJob = async {
                    NmsManagerRework.runSuspend()
                }

                awaitAll(databaseManagerInitJob, nmsManagerInitJob)

//                async {
//                    PermissionManager().runSuspend()
//                }.await()

                /* Stage 5: Initialize data key manager */
                DataKeyManager.runSuspend()
            }.await()
        }
    }

    override fun onEnable() {
        onLoadJob?.invokeOnCompletion {
//            Bukkit.getScheduler().runTask(pluginContext.mainPluginInstance, Runnable {
//                RoleManagerNew().run()
//            })
//            GuiManager().run()
//            PlayerSessionManagerNew().run()
//            CommandManager().run()

            pluginContext.getServer().consoleSender.sendMessage(NEON_ON_ENABLED_TITLE)
        }
    }

    override fun onDisable() {
        //neonDatabaseManager.close()
        PluginDIManager.close()
        pluginContext.getServer().consoleSender.sendMessage(NEON_ON_DISABLED_TITLE)
    }
}