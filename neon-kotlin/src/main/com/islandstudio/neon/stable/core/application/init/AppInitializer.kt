package com.islandstudio.neon.stable.core.application.init

import com.islandstudio.neon.Neon
import com.islandstudio.neon.stable.common.ColorPalette
import com.islandstudio.neon.stable.core.application.AppContext
import com.islandstudio.neon.stable.core.application.di.IComponentInjector
import com.islandstudio.neon.stable.core.application.extension.NeonExtensions
import com.islandstudio.neon.stable.core.database.DatabaseInterface
import com.islandstudio.neon.stable.core.database.DatabaseStructureInitializer
import com.islandstudio.neon.stable.core.io.resource.ResourceManager
import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.koin.core.annotation.Single
import org.koin.core.component.inject
import kotlin.math.roundToInt

@Single
class AppInitializer: IComponentInjector {
    companion object: IComponentInjector {
        private val neon by inject<Neon>()
        private val appContext by inject<AppContext>()

        private val neonVersionText = "${ColorPalette.CyanBlue.color}${ColorPalette.Bold.color}v${neon.description.version}${ColorPalette.Reset.color}"
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

        val NEON_ON_ENABLED_TITLE = "\n" + """
                 ${ColorPalette.Yellow.color}_____                                                                _____ 
                ${ColorPalette.Yellow.color}{_____}                                                              {_____}
                 ${ColorPalette.Purple.color}| ~ |${ColorPalette.Orange.color}~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~${ColorPalette.Purple.color}| ~ | 
                 ${ColorPalette.Purple.color}| ~ |  ${ColorPalette.LightGreen.color}+==========================================================+  ${ColorPalette.Purple.color}| ~ | 
                 ${ColorPalette.Purple.color}| ~ |          ${ColorPalette.CyanBlue.color}░███    ░██ ░███████    ░███    ░███    ░██           ${ColorPalette.Purple.color}| ~ | 
                 ${ColorPalette.Purple.color}| ~ |          ${ColorPalette.CyanBlue.color}░████   ░██ ░██       ░██  ░██  ░████   ░██           ${ColorPalette.Purple.color}| ~ |
                 ${ColorPalette.Purple.color}| ~ |          ${ColorPalette.CyanBlue.color}░██ ░██ ░██ ░██████  ░██    ░██ ░██ ░██ ░██           ${ColorPalette.Purple.color}| ~ | 
                 ${ColorPalette.Purple.color}| ~ |          ${ColorPalette.CyanBlue.color}░██   ░████ ░██       ░██  ░██  ░██   ░████           ${ColorPalette.Purple.color}| ~ |
                 ${ColorPalette.Purple.color}| ~ |          ${ColorPalette.CyanBlue.color}░██    ░███ ░███████    ░███    ░██    ░███           ${ColorPalette.Purple.color}| ~ | 
                 ${ColorPalette.Purple.color}| ~ |  ${ColorPalette.LightGreen.color}+==========================================================+  ${ColorPalette.Purple.color}| ~ |
                 ${ColorPalette.Purple.color}|___|${ColorPalette.Orange.color}~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~${ColorPalette.Purple.color}|___| 
                ${ColorPalette.Yellow.color}{_____}${ColorPalette.Reset.color}                  ()                     ()                   ${ColorPalette.Yellow.color}{_____}${ColorPalette.Reset.color}
                                         ++=====================++
                                         ||     ~ ${ColorPalette.Green.color}${ColorPalette.Bold.color}STARTED${ColorPalette.Reset.color} ~     ||
                                         ||   ~ $neonVersionText ~   ||
                                         ++=====================++
        """.trimIndent() + "\n"

        val NEON_ON_DISABLED_TITLE = "\n" + """
                 ${ColorPalette.Yellow.color}_____                                                                _____ 
                ${ColorPalette.Yellow.color}{_____}                                                              {_____}
                 ${ColorPalette.Purple.color}| ~ |${ColorPalette.Orange.color}~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~${ColorPalette.Purple.color}| ~ | 
                 ${ColorPalette.Purple.color}| ~ |  ${ColorPalette.LightGreen.color}+==========================================================+  ${ColorPalette.Purple.color}| ~ | 
                 ${ColorPalette.Purple.color}| ~ |          ${ColorPalette.CyanBlue.color}░███    ░██ ░███████    ░███    ░███    ░██           ${ColorPalette.Purple.color}| ~ | 
                 ${ColorPalette.Purple.color}| ~ |          ${ColorPalette.CyanBlue.color}░████   ░██ ░██       ░██  ░██  ░████   ░██           ${ColorPalette.Purple.color}| ~ |
                 ${ColorPalette.Purple.color}| ~ |          ${ColorPalette.CyanBlue.color}░██ ░██ ░██ ░██████  ░██    ░██ ░██ ░██ ░██           ${ColorPalette.Purple.color}| ~ | 
                 ${ColorPalette.Purple.color}| ~ |          ${ColorPalette.CyanBlue.color}░██   ░████ ░██       ░██  ░██  ░██   ░████           ${ColorPalette.Purple.color}| ~ |
                 ${ColorPalette.Purple.color}| ~ |          ${ColorPalette.CyanBlue.color}░██    ░███ ░███████    ░███    ░██    ░███           ${ColorPalette.Purple.color}| ~ | 
                 ${ColorPalette.Purple.color}| ~ |  ${ColorPalette.LightGreen.color}+==========================================================+  ${ColorPalette.Purple.color}| ~ |
                 ${ColorPalette.Purple.color}|___|${ColorPalette.Orange.color}~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~${ColorPalette.Purple.color}|___| 
                ${ColorPalette.Yellow.color}{_____}${ColorPalette.Reset.color}                  ()                     ()                   ${ColorPalette.Yellow.color}{_____}${ColorPalette.Reset.color}
                                         ++=====================++
                                         ||     ~ ${ColorPalette.Red.color}${ColorPalette.Bold.color}DISABLED${ColorPalette.Reset.color} ~    ||
                                         ||   ~ $neonVersionText ~   ||
                                         ++=====================++
        """.trimIndent() + "\n"



        /***
         * Register event processor. This is used when the server is starting up and only if the particular feature is enabled.
         */
        fun registerEventProcessor(eventProcessor: Listener) {
            HandlerList.getRegisteredListeners(neon).find {
                it.listener.javaClass.canonicalName == eventProcessor.javaClass.canonicalName
            }?.let { return }

            neon.server.pluginManager.registerEvents(eventProcessor, neon)
        }

        /***
         * Unregister event. This is used when certain feature is disabled.
         */
        fun unregisterEventProcessor(eventProcessor: Listener) {
            HandlerList.getRegisteredListeners(neon).forEach {
                if (it.listener.javaClass.canonicalName != eventProcessor.javaClass.canonicalName) return@forEach

                HandlerList.unregisterAll(it.listener)
            }
        }
    }

    /**
     * Pre-initialize core components that used in most of the features.
     *
     */
    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    fun preInit() {
        val appContext by inject<AppContext>()
        val jobContext = newSingleThreadContext("Neon Initializer (Pre-Staging)")

        jobContext.use { dispatcher ->
            CoroutineScope(dispatcher).async {
                neon.logger.info(appContext.getCodeMessage("neon.info.pre_init.start"))

                delay(500L)

                if (!appContext.isVersionCompatible) {
                    neon.logger.severe(appContext.getCodeMessage("neon.error.pre_init.incompatible_version"))
                    return@async
                }

                val preInitAppClasses = AppClasses.entries
                    .filter { it.initializationStage == InitializationStage.PRE_INIT }

                this.async {
                    ResourceManager.run()
                }.await()

                this.async(Dispatchers.IO) {
                    delay(500)

                    val databaseInterface by inject<DatabaseInterface>()

                    databaseInterface.connect()
                }.await()

                this.async(Dispatchers.IO) {
                    delay(500)

                    DatabaseStructureInitializer().initializeStructure()
                }.await()

                preInitAppClasses.forEachIndexed { index, appClass ->
                    val clazz = appClass.clazz
                    val loadingProgress: Int =  (((index + 1).toDouble() / preInitAppClasses.size.toDouble()) * 100).roundToInt()

                    val className: String = with(clazz.simpleName) {
                        if (this == "Handler" || this == "Companion") {
                            val splitClassNames = clazz.canonicalName.split(".")

                            return@with "${splitClassNames[splitClassNames.size - 2]}.${splitClassNames[splitClassNames.size - 1]}"
                        }

                        return@with this
                    }

                    runCatching {
                        if (className.contains("Companion")) {
                            val hasRunFunction = clazz.declaredMethods.map { lambdaClazz -> lambdaClazz.name }.contains("run")

                            if (hasRunFunction) {
                                clazz.getDeclaredMethod("run").invoke(clazz.enclosingClass.getField("Companion").get(null))
                            }

                            return@runCatching
                        }

                        /* For non-companion class */
                        val hasRunFunction = clazz.declaredMethods.map { lambdaClazz -> lambdaClazz.name }.contains("run")

                        if (hasRunFunction) {
                            clazz.getDeclaredMethod("run").invoke(clazz.getField("INSTANCE").get(null))
                        }
                    }.onFailure {
                        neon.logger.severe(it.cause?.stackTraceToString())
                        return@forEachIndexed
                    }.onSuccess {
                        //neon.logger.info(appContext.getFormattedCodeMessage("neon.info.pre_init.processing", loadingProgress))
                        delay(150L)
                    }
                }
            }.asCompletableFuture().join().also {
                if (appContext.isVersionCompatible) {
                    neon.logger.info(appContext.getCodeMessage("neon.info.pre_init.complete"))
                }
            }
        }
    }

    /**
     * Post-initialize components as well as the features.
     *
     */
    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    fun postInit() {
        neon.logger.info(appContext.getCodeMessage("neon.info.post_init.start"))
        Thread.sleep(500L)

        if (!appContext.isVersionCompatible) {
            neon.logger.severe(appContext.getCodeMessage("neon.error.post_init.incompatible_version"))
            //neon.logger.severe("Please check for the latest version of Neon plugin!")
            //neon.logger.warning("Supported Minecraft version: 1.17.X ~ 1.20.4")
            return
        }

        val jobContext = newSingleThreadContext("Neon Initializer (Post-Staging)")
        val postLoadAppClasses = AppClasses.entries
            .filter { it.initializationStage == InitializationStage.POST_INIT }

        postLoadAppClasses.forEachIndexed { index, appClazz ->
            val clazz = appClazz.clazz
            val loadingProgress: Int = (((index + 1).toDouble() / postLoadAppClasses.size.toDouble()) * 100).roundToInt()

            /* Check if the simple name of the class is equal to "Handler" or "Companion",
            * if so, it split the canonical name and get the last 2 parts.
            * E.g.: com.islandstudio.neon.stable.primary.nCommand.NCommand.Companion -> NCommand.Companion
            */
            val className: String = when {
                (clazz.simpleName == "Handler" || clazz.simpleName == "Companion") -> {
                    val splitClassNames = clazz.canonicalName.split(".")
                    "${splitClassNames[splitClassNames.size - 2]}.${splitClassNames[splitClassNames.size - 1]}"
                }

                else -> {
                    clazz.simpleName
                }
            }

            if (appClazz.isSynchronous) {
                runCatching {
                    when {
                        (className.contains("Companion")) -> {
                            clazz.getDeclaredMethod("run").invoke(clazz.enclosingClass.getField("Companion").get(null))
                        }

                        else -> {
                            clazz.getDeclaredMethod("run").invoke(clazz.getField("INSTANCE").get(null))
                        }
                    }
                }.onFailure {
                    neon.logger.severe(it.cause?.stackTraceToString())
                    return@forEachIndexed
                }.onSuccess {
                    neon.logger.info(appContext.getFormattedCodeMessage("neon.info.post_init.processing", loadingProgress))
                    Thread.sleep(150L)
                    return@forEachIndexed
                }
            }

            CoroutineScope(jobContext).async {
                runCatching {
                    when {
                        (className.contains("Companion")) -> {
                            clazz.getDeclaredMethod("run").invoke(clazz.enclosingClass.getField("Companion").get(null))
                        }

                        else -> {
                            clazz.getDeclaredMethod("run").invoke(clazz.getField("INSTANCE").get(null))
                        }
                    }
                }.onFailure {
                    neon.logger.severe(it.cause?.stackTraceToString())
                    return@async
                }.onSuccess {
                    neon.logger.info(appContext.getFormattedCodeMessage("neon.info.post_init.processing", loadingProgress))
                    delay(150L)
                }
            }.asCompletableFuture().get()
        }

        jobContext.close()
        neon.logger.info(appContext.getCodeMessage("neon.info.post_init.complete"))
    }


    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    fun reInit() {
        val jobContext = newSingleThreadContext("Neon Initializer (Re-staging)")

        AppClasses.entries
            .filter { it.isConfigReloadable && it.initializationStage == InitializationStage.POST_INIT }
            .forEach { appClassDetail ->
                val clazz = appClassDetail.clazz

                /* Check if the simple name of the class is equal to "Handler" or "Companion",
                * if so, it split the canonical name and get the last 2 parts.
                * E.g.: com.islandstudio.neon.stable.primary.nCommand.NCommand.Companion -> NCommand.Companion
                */
                val className: String = when {
                    (clazz.simpleName == "Handler" || clazz.simpleName == "Companion") -> {
                        val splitClassNames = clazz.canonicalName.split(".")
                        "${splitClassNames[splitClassNames.size - 2]}.${splitClassNames[splitClassNames.size - 1]}"
                    }

                    else -> {
                        appClassDetail.clazz.simpleName
                    }
                }

                if (appClassDetail.isSynchronous) {
                    runCatching {
                        when {
                            (className.contains("Companion")) -> {
                                clazz.getDeclaredMethod("run").invoke(clazz.enclosingClass.getField("Companion").get(null))
                            }

                            else -> {
                                clazz.getDeclaredMethod("run").invoke(clazz.getField("INSTANCE").get(null))
                            }
                        }
                    }.onFailure {
                        neon.logger.severe(it.cause?.stackTraceToString())
                        return@forEach
                    }.onSuccess {
                        Thread.sleep(150L)
                        return@forEach
                    }
                }

                CoroutineScope(jobContext).async {
                    runCatching {
                        when {
                            (className.contains("Companion")) -> {
                                clazz.getDeclaredMethod("run").invoke(clazz.enclosingClass.getField("Companion").get(null))
                            }

                            else -> {
                                clazz.getDeclaredMethod("run").invoke(clazz.getField("INSTANCE").get(null))
                            }
                        }
                    }.onFailure {
                        neon.logger.severe(it.cause?.stackTraceToString())
                        return@async
                    }.onSuccess {
                        delay(150L)
                        return@async
                    }
                }.asCompletableFuture().get()
            }

        jobContext.close()
    }

    fun loadExtension(neonExtension: NeonExtensions) {
        neon.logger.info("Loading extensions......")

        neon.pluginLoader.loadPlugin(neonExtension).also {
            neon.pluginLoader.enablePlugin(it)
        }
    }
}