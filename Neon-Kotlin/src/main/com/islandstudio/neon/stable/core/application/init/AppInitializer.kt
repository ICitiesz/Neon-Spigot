package com.islandstudio.neon.stable.core.application.init

import com.islandstudio.neon.Neon
import com.islandstudio.neon.stable.core.application.AppContext
import com.islandstudio.neon.stable.core.application.CompatibleVersions
import com.islandstudio.neon.stable.core.application.server.ServerProvider
import com.islandstudio.neon.stable.core.application.server.ServerRunningMode
import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture
import net.md_5.bungee.api.ChatColor
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.koin.core.component.inject
import kotlin.math.roundToInt

class AppInitializer {
    companion object: AppContext.Injector {
        private val neon by inject<Neon>()

        val serverProvider = with (neon.server.name) {
            ServerProvider.entries.find { it.name.equals(this, true) }!!
        }

        val serverVersion = neon.server.bukkitVersion.split("-").first()

        val serverRunningMode = if (neon.server.onlineMode) {
            ServerRunningMode.ONLINE
        } else { ServerRunningMode.OFFLINE }

        /* Title format palette */
        private val cyanBlue = ChatColor.of("#34baeb")
        private val orange = ChatColor.of("#f57d1f")
        private val lightGreen = ChatColor.of("#9bec00")
        private val purple = ChatColor.of("#892cdc")
        private val yellow = ChatColor.of("#ffed00")
        private val red = ChatColor.RED
        private val green = ChatColor.GREEN

        private val reset = ChatColor.RESET
        private val bold = ChatColor.BOLD

        private val neonVersionText = "$cyanBlue${bold}v${neon.description.version}$reset"
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
                 ${yellow}_____                                                                _____ 
                $yellow{_____}                                                              {_____}
                 $purple| ~ |$orange~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~$purple| ~ | 
                 $purple| ~ |  $lightGreen+==========================================================+  $purple| ~ | 
                 $purple| ~ |          ${cyanBlue}░███    ░██ ░███████    ░███    ░███    ░██           $purple| ~ | 
                 $purple| ~ |          ${cyanBlue}░████   ░██ ░██       ░██  ░██  ░████   ░██           $purple| ~ |
                 $purple| ~ |          ${cyanBlue}░██ ░██ ░██ ░██████  ░██    ░██ ░██ ░██ ░██           $purple| ~ | 
                 $purple| ~ |          ${cyanBlue}░██   ░████ ░██       ░██  ░██  ░██   ░████           $purple| ~ |
                 $purple| ~ |          ${cyanBlue}░██    ░███ ░███████    ░███    ░██    ░███           $purple| ~ | 
                 $purple| ~ |  $lightGreen+==========================================================+  $purple| ~ |
                 $purple|___|$orange~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~$purple|___| 
                $yellow{_____}$reset                  ()                     ()                   $yellow{_____}$reset
                                         ++=====================++
                                         ||     ~ $green${bold}STARTED$reset ~     ||
                                         ||   ~ $neonVersionText ~   ||
                                         ++=====================++
        """.trimIndent() + "\n"

        val NEON_ON_DISABLED_TITLE = "\n" + """
                 ${yellow}_____                                                                _____ 
                $yellow{_____}                                                              {_____}
                 $purple| ~ |$orange~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~$purple| ~ | 
                 $purple| ~ |  $lightGreen+==========================================================+  $purple| ~ | 
                 $purple| ~ |          ${cyanBlue}░███    ░██ ░███████    ░███    ░███    ░██           $purple| ~ | 
                 $purple| ~ |          ${cyanBlue}░████   ░██ ░██       ░██  ░██  ░████   ░██           $purple| ~ |
                 $purple| ~ |          ${cyanBlue}░██ ░██ ░██ ░██████  ░██    ░██ ░██ ░██ ░██           $purple| ~ | 
                 $purple| ~ |          ${cyanBlue}░██   ░████ ░██       ░██  ░██  ░██   ░████           $purple| ~ |
                 $purple| ~ |          ${cyanBlue}░██    ░███ ░███████    ░███    ░██    ░███           $purple| ~ | 
                 $purple| ~ |  $lightGreen+==========================================================+  $purple| ~ |
                 $purple|___|$orange~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~$purple|___| 
                $yellow{_____}$reset                  ()                     ()                   $yellow{_____}$reset
                                         ++=====================++
                                         ||     ~ $red${bold}DISABLED$reset ~    ||
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

        fun isCompatible(): Boolean {
            return CompatibleVersions.entries.any { serverVersion in it.versions }
        }
    }

    /**
     * Pre-initialize core components that used in most of the features.
     *
     */
    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    fun preInit() {
        val jobContext = newSingleThreadContext("Neon Initializer (Pre-Staging)")

        CoroutineScope(jobContext).async {
            neon.logger.info(AppContext.getCodeMessage("neon.info.preinit.start_preinit_message"))

            delay(500L)

            if (!isCompatible()) {
                neon.logger.severe(AppContext.getCodeMessage("neon.error.preinit.incompatible_version"))
                return@async
            }

            val preInitAppClasses = AppClasses.entries
                .filter { it.initializationStage == InitializationStage.PRE_INIT }

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
                    neon.logger.info("Filling up Neon......${loadingProgress}%")
                    delay(150L)
                }
            }
        }.asCompletableFuture().join().also {
            neon.logger.info("Neon has been filled up!")
            jobContext.close()
        }
    }

    /**
     * Post-initialize components as well as the features.
     *
     */
    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    fun postInit() {
        if (!isCompatible()) {
            neon.logger.severe("Neon could not be lit up: Incompatible Minecraft version!")
            neon.logger.severe("Please check for the latest version of Neon plugin!")
            neon.logger.warning("Supported Minecraft version: 1.17.X ~ 1.20.4")
            return
        }

        neon.logger.info("Start lighting up Neon......")
        Thread.sleep(500L)
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
                    neon.logger.info("Lighting up Neon......${loadingProgress}%")
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
                    neon.logger.info("Lighting up Neon......${loadingProgress}%")
                    delay(150L)
                }
            }.asCompletableFuture().get()
        }

        jobContext.close()
        neon.logger.info("Neon has been lit up!")
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
}