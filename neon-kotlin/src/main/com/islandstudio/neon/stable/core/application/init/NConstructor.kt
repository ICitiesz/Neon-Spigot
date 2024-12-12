package com.islandstudio.neon.stable.core.application.init

import com.islandstudio.neon.Neon
import com.islandstudio.neon.stable.core.application.di.IComponentInjector
import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture
import org.bukkit.ChatColor
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.koin.core.component.inject

@Deprecated("Revamped to [AppInitializer]")
object NConstructor: IComponentInjector {
    private val neon by inject<Neon>()
    private val neonLogger = neon.logger

    val isUsingPaperMC: Boolean = runCatching {
        Class.forName("io.papermc.paper.plugin.loader.PluginLoader")
    }.mapCatching { true }.getOrElse { false }

    private const val NEON_VERSION: String = "|--------------== Neon v1.11-pre_1 ==-------------------|"
    private val minorVersion: String = neon.server.bukkitVersion.split("-").first()
    private val majorVersion: String = minorVersion.split(".")[0] + "." + minorVersion.split(".")[1]

    private val isCompatible: Boolean = run {
        if (!inMajorVersionRange(SupportedVersions.entries
                .map { supportedVersion -> supportedVersion.majorVersion }.toTypedArray())
        ) return@run false

        if (isMinorEqualMajorVersion()) return@run true

        val currentMinorVersions = SupportedVersions.entries
                .find { supportedVersion -> majorVersion == supportedVersion.majorVersion }?.minorVersions ?: return@run false

        if (!inMinorVersionRange(currentMinorVersions)) return@run false

        true
    }

    /**
     * Checkf if the current major version in the given major version range
     *
     * @return True if it is, else false
     */
    fun inMajorVersionRange(majorVersionRange: Array<String>): Boolean = majorVersion in majorVersionRange

    /**
     * Checkf if the current minor version in the given minor version range
     *
     * @return True if it is, else false
     */
    fun inMinorVersionRange(minorVersionRange: Array<String>): Boolean = minorVersion in minorVersionRange

    fun isMinorEqualMajorVersion(): Boolean = minorVersion == majorVersion

    /**
     * Building and initialize feature in pre-build stage
     *
     */
    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    fun preBuild() {
        CoroutineScope(newSingleThreadContext("Neon Pre-construction")).async {
            neonLogger.info("Start filling up Neon......")

            delay(500L)

            if (!isCompatible) {
                neonLogger.severe("Neon could not be filled up: Incompatible Minecraft version!")
                return@async
            }

            val preloadAppClasses =  AppClasses.entries
                .filter { it.initializationStage == InitializationStage.PRE_INIT }

            preloadAppClasses.forEachIndexed { index, appClazz ->
                val clazz = appClazz.clazz
                val loadingProgress: Int = (((index + 1).toDouble() / preloadAppClasses.size.toDouble()) * 100).toInt()

                /* Check if the simple name of the class is equal to "Handler" or "Companion",
                * if so, it split the canonical name and get the last 2 parts.
                * E.g.: com.islandstudio.neon.stable.primary.nCommand.NCommand.Companion -> NCommand.Companion
                */
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
                            return@runCatching
                        }

                        clazz.constructors.first().newInstance()
                        return@runCatching
                    }

                    /* For non-companion class */
                    val hasRunFunction = clazz.declaredMethods.map { lambdaClazz -> lambdaClazz.name }.contains("run")

                    if (hasRunFunction) {
                        clazz.getDeclaredMethod("run").invoke(clazz.getField("INSTANCE").get(null))
                        return@runCatching
                    }

                    clazz.constructors.first().newInstance()
                }.onFailure {
                    neonLogger.severe(it.stackTraceToString())
                }.onSuccess {
                    neonLogger.info("Filling up Neon......${loadingProgress}%")
                    delay(150L)
                }
            }

            neonLogger.info("Neon has been filled up!")
        }.asCompletableFuture().get()
    }

    /**
     * Building and initialize feature in post-build stage
     *
     */
    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    fun postBuild() {
        if (!isCompatible) {
            neonLogger.severe("Neon could not be lit up: Incompatible Minecraft version!")
            neonLogger.severe("Please check for the latest version of Neon plugin!")
            neonLogger.warning("Supported Minecraft version: 1.17.X ~ 1.20.4")
            return
        }

        neonLogger.info("Start lighting up Neon......")
        Thread.sleep(500L)

        val postLoadAppClasses = AppClasses.entries
            .filter { it.initializationStage == InitializationStage.POST_INIT }

        postLoadAppClasses.forEachIndexed { index, appClazz ->
            val clazz = appClazz.clazz
            val loadingProgress: Int = (((index + 1).toDouble() / postLoadAppClasses.size.toDouble()) * 100).toInt()

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
                    neonLogger.severe(it.stackTraceToString())
                }.onSuccess {
                    neonLogger.info("Lighting up Neon......${loadingProgress}%")
                    Thread.sleep(150L)
                    return@forEachIndexed
                }
            }

            CoroutineScope(newSingleThreadContext("Neon Post-construction")).async {
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
                    neonLogger.severe(it.stackTraceToString())
                }.onSuccess {
                    neonLogger.info("Lighting up Neon......${loadingProgress}%")
                    delay(150L)
                }
            }.asCompletableFuture().get()
        }

        neonLogger.info("Neon has been lit up!")
        displayStartingTitle()
    }

    /**
     * Rebuild all the components that are config-reloadable.
     *
     */
    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    fun rebuild() {
        AppClasses.entries
            .filter { it.isConfigReloadable && it.initializationStage == InitializationStage.POST_INIT }
            .forEach { applicationClassDetail ->
                val clazz = applicationClassDetail.clazz

                /* Check if the simple name of the class is equal to "Handler" or "Companion",
                * if so, it split the canonical name and get the last 2 parts.
                * E.g.: com.islandstudio.neon.stable.primary.nCommand.NCommand.Companion -> NCommand.Companion
                */
                val className: String = when {
                    (clazz.simpleName == "Handler" || clazz.simpleName == "Companion") -> {
                        val splitClassNames = applicationClassDetail.clazz.canonicalName.split(".")
                        "${splitClassNames[splitClassNames.size - 2]}.${splitClassNames[splitClassNames.size - 1]}"
                    }

                    else -> {
                        applicationClassDetail.clazz.simpleName
                    }
                }

                if (applicationClassDetail.isSynchronous) {
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
                        neonLogger.severe(it.stackTraceToString())
                    }.onSuccess {
                        Thread.sleep(150L)
                        return@forEach
                    }
                }

                CoroutineScope(newSingleThreadContext("Neon Rebuild")).async {
                    runCatching {
                        when {
                            (className.contains("Companion")) -> {
                                applicationClassDetail.clazz.getDeclaredMethod("run").invoke(applicationClassDetail.clazz.enclosingClass.getField("Companion").get(null))
                            }

                            else -> {
                                applicationClassDetail.clazz.getDeclaredMethod("run").invoke(applicationClassDetail.clazz.getField("INSTANCE").get(null))
                            }
                        }
                    }.onFailure {
                        neonLogger.severe(it.stackTraceToString())
                    }.onSuccess {
                        delay(150L)
                    }
                }.asCompletableFuture().get()
            }
    }

    /**
     * Get major server version. E.g: 1.17, 1.18, 1.19 ...
     *
     * @return
     */
    fun getMajorVersion(): String = majorVersion

    /**
     * Get minor server version. E.g: 1.17.1 1.18.2, 1.19.4
     *
     * @return
     */
    fun getMinorVersion(): String = minorVersion

    /***
     * Register event processor. This is used when the server is starting up and only if the particular feature is enabled.
     */
    fun registerEventProcessor(eventProcessor: Listener) {
//        HandlerList.getRegisteredListeners(neon).forEach {
//            if (it.listener.javaClass.canonicalName == eventProcessor.javaClass.canonicalName) return
//        }

        HandlerList.getRegisteredListeners(neon).find {
            it.listener.javaClass.canonicalName == eventProcessor.javaClass.canonicalName
        }?.let { return }

        neon.server.pluginManager.registerEvents(eventProcessor, neon)
    }

    /***
     * Unregister event. This is used when certain feature is disabled.
     */
    fun unRegisterEventProcessor(eventListener: Listener) {
        HandlerList.getRegisteredListeners(neon).forEach {
            if (it.listener.javaClass.canonicalName != eventListener.javaClass.canonicalName) return@forEach

            HandlerList.unregisterAll(it.listener)
        }
    }

    private fun displayStartingTitle() {
        neon.server.consoleSender.sendMessage(
            "${ChatColor.GOLD}|+++++++++++++++++=================+++++++++++++++++|"
        )
        neon.server.consoleSender.sendMessage(
            "${ChatColor.GOLD}|-----------------=================-----------------|"
        )
        neon.server.consoleSender.sendMessage(
            "${ChatColor.GOLD}$NEON_VERSION"
        )
        neon.server.consoleSender.sendMessage(
            "${ChatColor.GOLD}|-----------------===${ChatColor.GREEN} <Started> ${ChatColor.GOLD}===-----------------|"
        )
        neon.server.consoleSender.sendMessage(
            "${ChatColor.GOLD}|-----------------=================-----------------|"
        )
        neon.server.consoleSender.sendMessage(
            "${ChatColor.GOLD}|+++++++++++++++++=================+++++++++++++++++|"
        )
    }

    fun displayClosingTitle() {
        neon.server.consoleSender.sendMessage(
            "${ChatColor.GOLD}|+++++++++++++++++=================+++++++++++++++++|"
        )
        neon.server.consoleSender.sendMessage(
            "${ChatColor.GOLD}|-----------------=================-----------------|"
        )
        neon.server.consoleSender.sendMessage(
            "${ChatColor.GOLD}$NEON_VERSION"
        )
        neon.server.consoleSender.sendMessage(
            "${ChatColor.GOLD}|-----------------===${ChatColor.RED} <Stopped> ${ChatColor.GOLD}===-----------------|"
        )
        neon.server.consoleSender.sendMessage(
            "${ChatColor.GOLD}|-----------------=================-----------------|"
        )
        neon.server.consoleSender.sendMessage(
            "${ChatColor.GOLD}|+++++++++++++++++=================+++++++++++++++++|"
        )
    }
}