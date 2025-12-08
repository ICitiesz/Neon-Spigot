package com.islandstudio.neon

import com.islandstudio.neon.core.di.module.NeonModule
import com.islandstudio.neon.shared.core.di.GlobalDIManager
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.di.SharedModule
import com.islandstudio.neon.shared.core.di.module.NeonAPIModule
import com.islandstudio.neon.shared.core.exception.NeonException
import com.islandstudio.neon.shared.core.initialization.IPluginInitializer
import com.islandstudio.neon.shared.core.initialization.PluginContext
import com.islandstudio.neon.shared.core.io.resource.library.NeonLibraryManager
import com.islandstudio.neon.shared.experimental.NeonClassLoader
import com.islandstudio.neon.shared.experimental.utils.coroutines.CloseableCoroutineScope
import com.islandstudio.neon.shared.utils.data.DataUtil
import com.islandstudio.neon.shared.utils.data.IObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.inject
import org.koin.ksp.generated.module
import java.io.File
import java.lang.reflect.Proxy

class Neon : JavaPlugin(), IComponentInjector, IObjectMapper {
    private val neonPluginInitializerClassPath = "com.islandstudio.neon.core.initialization.NeonPluginInitializer"

    private val mainPluginContext = PluginContext(this, this.file)
    private val initCloseableCoroutineScope = CloseableCoroutineScope(Dispatchers.IO)
    private lateinit var neonClassLoader: NeonClassLoader
    private lateinit var neonPluginInitializer: IPluginInitializer

    //private val neonPluginLoader by inject<NeonPluginLoader>()


    private var isPreLoaded = false
    private var isPostLoaded = false
    var isServerLoaded = false


    init {
        GlobalDIManager.startGlobalScoped(
            mainPluginContext,
            NeonModule().module,
            SharedModule().module,
            NeonAPIModule().module,
        )
    }

    override fun onLoad() {
        initCloseableCoroutineScope.launchJob {
            async {
                mainPluginContext.loadCodeMessages()
            }.await()

            async {
                mainPluginContext.resourceManager.initialize()
            }.await()

            /* Get libraries */
            val libraries: ArrayList<File> = async {
                return@async NeonLibraryManager(mainPluginContext).getLibraries()
            }.await()

            /* Build neon classloader with the libraries */
            logger.info("Building Neon class loader...")
            neonClassLoader = NeonClassLoader.buildNeonClassLoader(
                libraries.toTypedArray(),
                this@Neon.classLoader,
                arrayOf()
            )

            /* Load Neon Plugin Initializer */
            logger.info("Loading Neon plugin initializer...")
            neonPluginInitializer = runCatching {
                val scopedPluginContext = neonClassLoader.loadClass(PluginContext::class.java.name, true).run {
                    this.getDeclaredConstructor(Plugin::class.java, File::class.java).newInstance(this@Neon, this@Neon.file)
                }

                val pluginInitializerProxy = neonClassLoader.loadClass(neonPluginInitializerClassPath).declaredConstructors.first()
                    .newInstance(scopedPluginContext).run {
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

                DataUtil.asType<IPluginInitializer>(pluginInitializerProxy)
            }.getOrElse {
                throw NeonException("Failed to load Neon plugin initializer", it)
            }
        }.invokeOnCompletion {
            neonPluginInitializer.onLoad()
        }

        //neonPluginLoader.preLoad().apply { isPreLoaded = this }

//        val scope = CoroutineScope(newSingleThreadContext("Test Non-blocking Concurent")).async {
//            logger.info("Simulating task execution......")
//            neonLibraryManager.loadLibrary()
//            delay(25000)
//        }.invokeOnCompletion {
//            isServerLoaded = true
//            logger.info { "Simulation completed!" }
//            NmsManager.run()
//        }
    }

    /*
    * TODO:
    *  - Planning on new database approach.
    *  - This small test used to simulate database update/migration execution on a non-block coroutine scope
    *  - While the database update/migration is running, non-dependent process will continue.
    *  - Meanwhile, players are not able to join server until the server fully intialize (database update in this case)
    * */
    class TestEventClass(): IComponentInjector, Listener {
        private val neon by inject<Neon>()

        @EventHandler
        private fun onPlayerJoin(e: PlayerLoginEvent) {
            if (neon.isServerLoaded) return

            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Server still intializing, please try again later!")
        }
    }

    //private fun getPluginClassLoader() = this.classLoader

    override fun onEnable() {
        //server.pluginManager.registerEvents(TestEventClass(), this)
        neonPluginInitializer.onEnable()

        //neonPluginLoader.postLoad().apply { isPostLoaded = this }

        //if (!(isPreLoaded && isPostLoaded)) return

//        logger.info("Pre-caching some data from Neon Database...").apply {
//            val databaseInfoAdapter by inject<DatabaseInfoAdapter>()
//
//            if (databaseInfoAdapter.cachingData()) {
//                logger.info("Pre-caching data completed!")
//            } else {
//                logger.warning("Pre-caching data failed!")
//            }
//        }
        //server.consoleSender.sendMessage(NeonPluginLoader.NEON_ON_ENABLED_TITLE)
    }

    override fun onDisable() {
        neonPluginInitializer.onDisable()
        neonClassLoader.close()

        if (!(isPreLoaded && isPostLoaded)) return
    }

//    @JvmName("getNeonAppLoader")
//    fun getAppLoader(): NeonPluginLoader = this.neonPluginLoader
}