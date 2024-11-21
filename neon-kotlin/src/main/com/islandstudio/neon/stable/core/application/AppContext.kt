package com.islandstudio.neon.stable.core.application

import com.islandstudio.neon.Neon
import com.islandstudio.neon.stable.core.application.di.ModuleInjector
import com.islandstudio.neon.stable.core.application.server.ServerProvider
import com.islandstudio.neon.stable.core.application.server.ServerRunningMode
import com.islandstudio.neon.stable.core.io.resource.NeonResources
import com.islandstudio.neon.stable.core.io.resource.ResourceManager
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import org.koin.core.annotation.Single
import org.koin.core.component.inject
import java.util.*

@Single
class AppContext: ModuleInjector {
    private val neon by inject<Neon>()

    private val codeMessages = Properties()
    private val envValues: Dotenv = dotenv {
        this.directory = "/resources/application"
        this.filename = ".env"
        this.ignoreIfMalformed = true
        this.ignoreIfMissing = true
    }

    val serverProvider = ServerProvider.entries.first { it.name.equals(neon.server.name, true) }
    val serverVersion = neon.server.bukkitVersion.split("-").first()
    val serverRunningMode = if (neon.server.onlineMode) ServerRunningMode.ONLINE else ServerRunningMode.OFFLINE

    fun getAppEnvValue(key: String): String = envValues.get(key)

    fun loadCodeMessages() {
        with(ResourceManager().getNeonResourceAsStream(NeonResources.NEON_CODE_MESSAGES)) {
            use {
                codeMessages.load(it)
            }
        }
    }

    fun getCodeMessage(code: String): String = codeMessages.getProperty(code) ?: code
}


