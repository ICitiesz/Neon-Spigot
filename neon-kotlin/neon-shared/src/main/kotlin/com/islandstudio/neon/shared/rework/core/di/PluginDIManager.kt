package com.islandstudio.neon.shared.rework.core.di

import com.islandstudio.neon.shared.rework.core.di.module.SharedComponentModule
import com.islandstudio.neon.shared.rework.core.initialization.context.IPluginContext
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

        pluginKoinApp?.let {
            KoinContextRegistry.registerContext(this.javaClass.classLoader, it.koin)
        }
    }

    fun getKoin(): Koin = pluginKoinApp?.koin ?: throw IllegalStateException("Plugin dependency injection not initialized!")

    fun close() {
        pluginKoinApp?.close()
        pluginKoinApp = null
    }
}