package com.islandstudio.neon

import com.islandstudio.neon.core.di.module.NeonModule
import com.islandstudio.neon.rework.core.initialization.PluginInitializer
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.exception.NeonException
import com.islandstudio.neon.shared.experimental.utils.coroutines.CloseableCoroutineScope
import com.islandstudio.neon.shared.rework.core.di.BootstrapDIManager
import com.islandstudio.neon.shared.rework.core.initialization.IPluginInitializer
import com.islandstudio.neon.shared.rework.core.initialization.NeonClassLoader
import com.islandstudio.neon.shared.rework.core.initialization.context.PluginContext
import com.islandstudio.neon.shared.rework.core.io.ExternalLibraryManager
import com.islandstudio.neon.shared.utils.data.IObjectMapper
import kotlinx.coroutines.Dispatchers
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.inject
import org.koin.ksp.generated.module
import java.io.File

class Neon : JavaPlugin(), IComponentInjector, IObjectMapper {

    private val bootstrapScopedPluginContext = PluginContext(this, this.file)
    private val initCloseableCoroutineScope = CloseableCoroutineScope(Dispatchers.IO)
    private lateinit var neonClassLoader: NeonClassLoader
    private lateinit var pluginInitializer: IPluginInitializer

    //private val neonPluginLoader by inject<NeonPluginLoader>()


    private var isPreLoaded = false
    private var isPostLoaded = false
    var isServerLoaded = false


    init {
        BootstrapDIManager.start(
            bootstrapScopedPluginContext,
            NeonModule().module,
            //NeonAPIModule().module,
        )
    }

    override fun onLoad() {
        initCloseableCoroutineScope.launchJob {
            bootstrapScopedPluginContext.loadCodeMessages() // Should be loaded first as most of log may depends on it
            bootstrapScopedPluginContext.resourceManager.initialize()

            val externalLibraryManager = ExternalLibraryManager(bootstrapScopedPluginContext)

            /* Register external libraries */
            val externalLibraries: Array<File> = externalLibraryManager.registerLibrary()

            /* Build neon classloader with the external libraries */
            neonClassLoader = NeonClassLoader.buildClassLoader(
                bootstrapScopedPluginContext,
                externalLibraries,
                this@Neon.classLoader,
                arrayOf()
            )

            /* Load plugin initializer */
            pluginInitializer = runCatching {
                PluginInitializer.load(this@Neon, this@Neon.file, neonClassLoader)
            }.getOrElse {
                throw NeonException("Failed to load plugin initializer", it)
            }
        }.invokeOnCompletion {
            pluginInitializer.onLoad()
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
        pluginInitializer.onEnable()

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
        pluginInitializer.onDisable()
        neonClassLoader.close()

        if (!(isPreLoaded && isPostLoaded)) return
    }

//    @JvmName("getNeonAppLoader")
//    fun getAppLoader(): NeonPluginLoader = this.neonPluginLoader
}