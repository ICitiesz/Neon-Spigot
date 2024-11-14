package com.islandstudio.neon.stable.core.application

import com.islandstudio.neon.Neon
import com.islandstudio.neon.stable.core.application.di.ModuleInjector
import com.islandstudio.neon.stable.core.io.resource.NeonResources
import com.islandstudio.neon.stable.core.io.resource.ResourceManager
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import org.koin.core.component.inject
import java.util.*

object AppContext: ModuleInjector {
    private val codeMessages = Properties()
    private val envValues: Dotenv = dotenv {
        this.directory = "/resources/application"
        this.filename = ".env"
        this.ignoreIfMalformed = true
        this.ignoreIfMissing = true
    }

    fun getAppEnvValue(key: String): String = envValues.get(key)

    fun loadCodeMessages() {
        with(ResourceManager().getNeonResourceAsStream(NeonResources.NEON_CODE_MESSAGES)) {
            use {
                codeMessages.load(it)
            }
        }
    }

    fun getCodeMessage(code: String): String = codeMessages.getProperty(code) ?: code

    fun loadExtension(neonExtension: NeonExtensions) {
        val neon by inject<Neon>()

        neon.pluginLoader.loadPlugin(neonExtension).also {
           neon.pluginLoader.enablePlugin(it)
        }
    }
}


