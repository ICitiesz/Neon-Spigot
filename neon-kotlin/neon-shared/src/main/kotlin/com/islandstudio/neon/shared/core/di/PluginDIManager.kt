package com.islandstudio.neon.shared.core.di

import com.islandstudio.neon.shared.core.exception.NeonException
import com.islandstudio.neon.shared.core.initialization.IPluginContext
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.module.Module

object PluginDIManager {
    private var pluginKoinApp: KoinApplication? = null

    fun startPluginScoped(pluginContext: IPluginContext, vararg modules: Module) {
        //pluginContext.mainPluginInstance.logger.info("Starting Plugin DI Manager...")

        pluginKoinApp = KoinApplication.init().modules(*modules)
        pluginKoinApp!!.koin.declare(pluginContext)
    }

    fun getKoin(): Koin = pluginKoinApp?.koin ?: throw NeonException("Plugin DI Manager is not started.")

    fun closeScoped() {
        pluginKoinApp?.close()
        pluginKoinApp = null
    }
}