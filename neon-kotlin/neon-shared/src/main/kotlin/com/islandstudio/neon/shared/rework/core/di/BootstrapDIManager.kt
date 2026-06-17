package com.islandstudio.neon.shared.rework.core.di

import com.islandstudio.neon.shared.core.initialization.IPluginContext
import com.islandstudio.neon.shared.rework.core.di.module.SharedComponentModule
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.ksp.generated.koinApplication
import org.koin.core.annotation.KoinApplication as KoinApplicationAnnotation

@KoinApplicationAnnotation(modules = [SharedComponentModule::class])
object BootstrapDIManager {
    private var koinApp: KoinApplication? = null

    fun start(pluginContext: IPluginContext, vararg additionalModules: Module) {
        pluginContext.mainPluginInstance.logger.info("Initializing bootstrap dependency injection...")

        koinApp = this.koinApplication {
            this.modules(*additionalModules)

            this.koin.declare(pluginContext)
        }
    }

    fun getKoin(): Koin = koinApp?.koin ?: throw IllegalStateException("Bootstrap dependency injection not initialized!")

    fun close() {
        koinApp?.close()
        koinApp = null
    }

}