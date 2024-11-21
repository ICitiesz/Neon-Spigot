package com.islandstudio.neon.stable.core.application

import com.islandstudio.neon.stable.core.application.di.ModuleInjector
import com.islandstudio.neon.stable.core.application.server.ServerRunningMode
import org.koin.core.component.inject

object NeonAPI: ModuleInjector {
    private val appContext by inject<AppContext>()

    fun getServerRunningMode(): ServerRunningMode {
        return appContext.serverRunningMode
    }
}