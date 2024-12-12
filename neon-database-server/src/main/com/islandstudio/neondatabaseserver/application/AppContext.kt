package com.islandstudio.neondatabaseserver.application

import com.islandstudio.neon.stable.core.io.resource.NeonInternalResource
import com.islandstudio.neon.stable.core.io.resource.ResourceManager
import com.islandstudio.neondatabaseserver.NeonDatabaseServer
import com.islandstudio.neondatabaseserver.application.di.IComponentInjector
import io.github.cdimascio.dotenv.dotenv
import org.koin.core.annotation.Single
import org.koin.core.component.inject
import java.util.*

@Single
class AppContext: IComponentInjector  {
    private val neonDbServer by inject<NeonDatabaseServer>()

    private val codeMessages = Properties()
    private val envValues = dotenv {
        this.directory = "/resources/application"
        this.filename = ".env"
        this.ignoreIfMalformed = true
        this.ignoreIfMissing = true
    }

    fun getEnvValue(key: String): String = envValues.get(key)

    fun loadCodeMessages() {
        with(ResourceManager().getNeonResourceAsStream(NeonInternalResource.NeonDBServerCodeMessages, neonDbServer.getPluginClassLoader())) {
            use {
                codeMessages.load(it)
            }
        }
    }

    fun getCodeMessage(code: String): String = codeMessages.getProperty(code) ?: code
}