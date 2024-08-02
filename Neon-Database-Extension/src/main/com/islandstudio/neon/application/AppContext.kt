package com.islandstudio.neon.application

import com.islandstudio.neon.NeonDatabaseExtension
import com.islandstudio.neon.stable.core.io.resource.NeonResources
import com.islandstudio.neon.stable.core.io.resource.ResourceManager
import io.github.cdimascio.dotenv.dotenv
import org.bukkit.plugin.java.JavaPlugin.getPlugin
import org.hsqldb.server.Server
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.dsl.module
import java.util.*

object AppContext {
    private val koinApplication = KoinApplication.init()
    private val codeMessages = Properties()
    private val enVValues = dotenv {
        this.directory = "/resources/application"
        this.filename = ".env"
        this.ignoreIfMalformed = true
        this.ignoreIfMissing = true
    }

    private val generalModules = module {
        single<NeonDatabaseExtension> { getPlugin(NeonDatabaseExtension::class.java) }
        single<Server> { Server() }
    }

    fun loadModuleInjection() {
        koinApplication.modules(
            generalModules
        ).createEagerInstances()
    }

    fun getAppEnvValue(key: String): String = enVValues.get(key)

    fun loadCodeMessages() {
        val dbExtension by koinApplication.koin.inject<NeonDatabaseExtension>()

        with(
            ResourceManager().getNeonResourceAsStream(
            NeonResources.NEON_DATABASE_CODE_MESSAGES,
            dbExtension.getPluginClassLoader())
        ) {
            this?.let {
                use {
                    codeMessages.load(it)
                }
            }
        }
    }

    fun getCodeMessages(code: String): String = codeMessages.getProperty(code) ?: code

    interface Injector: KoinComponent {
        override fun getKoin(): Koin = koinApplication.koin
    }
}