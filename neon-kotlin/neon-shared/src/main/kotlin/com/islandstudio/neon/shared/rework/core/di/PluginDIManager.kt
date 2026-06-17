package com.islandstudio.neon.shared.rework.core.di

import com.islandstudio.neon.shared.core.initialization.IPluginContext
import com.islandstudio.neon.shared.rework.core.di.module.SharedComponentModule
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.ksp.generated.koinApplication
import org.koin.core.annotation.KoinApplication as KoinApplicationAnnotation

@KoinApplicationAnnotation(modules = [SharedComponentModule::class])
object PluginDIManager {
    private var pluginKoinApp: KoinApplication? = null

    fun start(pluginContext: IPluginContext, vararg additionalModules: Module) {
        pluginContext.mainPluginInstance.logger.info("Initializing plugin dependency injection...")

        pluginKoinApp = this.koinApplication {
            this.modules(*additionalModules)

            this.koin.declare(pluginContext)
        }
    }

    fun getKoin(): Koin = pluginKoinApp?.koin ?: throw IllegalStateException("Plugin dependency injection not initialized!")

    fun close() {
        pluginKoinApp?.close()
        pluginKoinApp = null
    }
}