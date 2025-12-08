package com.islandstudio.neon.shared.core.di

import com.islandstudio.neon.shared.core.exception.NeonException
import com.islandstudio.neon.shared.core.initialization.IPluginContext
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.context.GlobalContext
import org.koin.core.module.Module

object GlobalDIManager {
    private var koinApp: KoinApplication? = null

    fun startGlobalScoped(pluginContext: IPluginContext, vararg modules: Module) {
        pluginContext.mainPluginInstance.logger.info("Starting Global DI Manager...")

        koinApp = KoinApplication.init().modules(*modules)

        GlobalContext.startKoin(koinApp!!)
        koinApp!!.koin.declare(pluginContext)
    }

    fun getKoin(): Koin = koinApp?.koin ?: throw NeonException("Global DI Manager is not started.")

    fun close() {
        koinApp?.close()
        koinApp = null
    }

}