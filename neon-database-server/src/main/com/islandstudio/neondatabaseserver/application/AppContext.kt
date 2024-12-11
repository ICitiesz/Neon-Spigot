package com.islandstudio.neondatabaseserver.application

import com.islandstudio.neon.stable.core.io.resource.NeonInternalResources
import com.islandstudio.neon.stable.core.io.resource.ResourceManager
import com.islandstudio.neondatabaseserver.NeonDatabaseServer
import com.islandstudio.neondatabaseserver.application.di.ModuleInjector
import io.github.cdimascio.dotenv.dotenv
import org.koin.core.annotation.Single
import org.koin.core.component.inject
import java.util.*

@Single
class AppContext: ModuleInjector  {
    private val neonDbServer by inject<NeonDatabaseServer>()

    private val codeMessages = Properties()
    private val enVValues = dotenv {
        this.directory = "/resources/application"
        this.filename = ".env"
        this.ignoreIfMalformed = true
        this.ignoreIfMissing = true
    }

    fun getAppEnvValue(key: String): String = enVValues.get(key)

    fun loadCodeMessages() {
        with(ResourceManager().getNeonResourceAsStream(NeonInternalResources.NeonDatabaseServerCodeMessages, neonDbServer.getPluginClassLoader())) {
            use {
                codeMessages.load(it)
            }
        }
    }

    fun getCodeMessage(code: String): String = codeMessages.getProperty(code) ?: code
}