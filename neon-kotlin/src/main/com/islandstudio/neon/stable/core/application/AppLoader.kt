package com.islandstudio.neon.stable.core.application

import com.islandstudio.neon.Neon
import com.islandstudio.neon.shared.core.AppContext
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.io.resource.NeonExtensions
import com.islandstudio.neon.shared.core.io.resource.ResourceManager
import com.islandstudio.neon.stable.common.ColorPalette
import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.koin.core.annotation.Single
import org.koin.core.component.inject

@Single
class AppLoader: IComponentInjector {
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
    fun preLoad(): Boolean {
        appContext.loadCodeMessages()

        neon.logger.info(appContext.getCodeMessage("neon.info.pre_load.start"))
        neon.logger.info(appContext.getCodeMessage("neon.info.apploader.version_check"))

        Thread.sleep(300)

        appContext.ensureVersionCompatible() // Version check

        val jobContext = newSingleThreadContext("Neon App Loader (Pre-load Stage)")

        jobContext.use { dispatcher ->
            CoroutineScope(dispatcher).async {
                /* 1: Initialize resource */
                this.async(Dispatchers.IO) {
                    ResourceManager().initializeResource()
                    delay(200)
                }.await()

                /* 2: Load extension */
                this.async(Dispatchers.IO) {
                    loadExtension(NeonExtensions.NeonDatabaseExtension)
                    delay(200)
                }.await()

                /* 3: Establish database connection */
//                this.async(Dispatchers.IO) {
//                    val databaseInterface by inject<DatabaseInterface>()
//
//                    databaseInterface.connect()
//                    delay(200)
//                }.await()
//
//                /* 4: Initialize database structure */
//                this.async(Dispatchers.IO) {
//                    DatabaseStructureInitializer().initializeStructure()
//                    delay(200)
//                }.await()

                return@async

                val preLoadAppClasses = AppClasses.getPreLoadClasses()

                preLoadAppClasses.forEach { appClazz ->
                    runCatching {
                        AppClasses.invokeFunction(appClazz).apply {
                            if (!this) return@runCatching
                        }
                    }.onFailure {
                        neon.logger.severe(it.cause?.stackTraceToString())
                        return@forEach
                    }.onSuccess {
                        delay(120)
                    }
                }
            }.asCompletableFuture().join().also {
                neon.logger.info("${ColorPalette.Green.color}${appContext.getCodeMessage("neon.info.pre_load.complete")}")
                return true
            }
        }
    }

    /**
     * Post-initialize components as well as the features.
     *
     */
    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    fun postLoad(): Boolean {
        neon.logger.info(appContext.getCodeMessage("neon.info.post_load.start"))
        neon.logger.info(appContext.getCodeMessage("neon.info.apploader.version_check"))

        Thread.sleep(300)

        appContext.ensureVersionCompatible() // Version check

        val jobContext = newSingleThreadContext("Neon App Loader (Post-load Stage)")
        val postLoadAppClasses = AppClasses.getPostLoadClasses()

        jobContext.use { dispatcher ->
            postLoadAppClasses.forEach { appClazz ->
                if (appClazz.isSynchronous) {
                    runCatching {
                        AppClasses.invokeFunction(appClazz).apply {
                            if (!this) return@runCatching
                        }
                    }.onFailure {
                        neon.logger.severe(it.cause?.stackTraceToString())
                        return@forEach
                    }.onSuccess {
                        Thread.sleep(120)
                        return@forEach
                    }
                }

                CoroutineScope(dispatcher).async {
                    runCatching {
                        AppClasses.invokeFunction(appClazz).apply {
                            if (!this) return@runCatching
                        }
                    }.onFailure {
                        neon.logger.severe(it.cause?.stackTraceToString())
                        return@async
                    }.onSuccess {
                        delay(120)
                    }
                }.asCompletableFuture().join()
            }
        }

        neon.logger.info(appContext.getCodeMessage("neon.info.post_load.complete"))
        return true
    }


    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    fun reInit() {
        val jobContext = newSingleThreadContext("Neon Initializer (Re-staging)")

        AppClasses.entries
            .filter { it.isConfigReloadable && it.loadStage == LoadStage.PostLoad }
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