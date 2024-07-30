package com.islandstudio.neon.stable.core.application

import com.islandstudio.neon.Neon
import com.islandstudio.neon.stable.core.database.DatabaseConnector
import com.islandstudio.neon.stable.core.database.repository.AccessPermissionRepository
import com.islandstudio.neon.stable.core.database.repository.PlayerProfileRepository
import com.islandstudio.neon.stable.core.database.repository.RoleAccessRepository
import com.islandstudio.neon.stable.core.database.repository.RoleRepository
import com.islandstudio.neon.stable.core.io.ResourceManager
import com.islandstudio.neon.stable.core.io.resource.NeonResources
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import org.bukkit.plugin.java.JavaPlugin.getPlugin
import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.*

object AppContext {
    private val koinApplication = KoinApplication.init()
    private val codeMessages = Properties()
    private val envValues: Dotenv = dotenv {
        this.directory = "/resources/application"
        this.filename = ".env"
        this.ignoreIfMalformed = true
        this.ignoreIfMissing = true
    }

    private val generalModules = module {
        single<Neon> { getPlugin(Neon::class.java) }
        single<ServerRunningMode>(named<ServerRunningMode>()) {
            if (get<Neon>().server.onlineMode) {
                return@single ServerRunningMode.ONLINE
            }

            ServerRunningMode.OFFLINE
        }
    }

    private val databaseModules = module {
        single<DSLContext> { DSL.using(DatabaseConnector.hikariDataSource, SQLDialect.HSQLDB) }
        single<Configuration> { DSLContext::configuration.invoke(get<DSLContext>()) }
    }

    private val repositoryModules = module {
        single<PlayerProfileRepository> { PlayerProfileRepository() }
        single<RoleRepository> { RoleRepository() }
        single<AccessPermissionRepository> { AccessPermissionRepository() }
        single<RoleAccessRepository> { RoleAccessRepository() }
    }

    fun loadModuleInjection() {
        koinApplication.modules(
            generalModules,
            databaseModules,
            repositoryModules
        ).createEagerInstances()
    }

    fun unloadModuleInjection() {
        stopKoin()
    }

    fun getKoinApp(): KoinApplication = koinApplication

    fun getAppEnvValue(key: String): String = envValues.get(key)

    fun loadCodeMessages() {
        with(ResourceManager().getNeonResourceAsStream(NeonResources.NEON_CODE_MESSAGES)) {
            this?.let {
                use {
                    codeMessages.load(it)
                }
            }
        }
    }

    fun getCodeMessage(code: String): String = codeMessages.getProperty(code)

    fun loadExtension(neonExtension: NeonExtensions) {
        val neon by koinApplication.koin.inject<Neon>()

       neon.pluginLoader.loadPlugin(neonExtension).also {
           neon.pluginLoader.enablePlugin(it)
        }
    }

    interface Injector: KoinComponent {
        override fun getKoin(): Koin = koinApplication.koin
    }
}


