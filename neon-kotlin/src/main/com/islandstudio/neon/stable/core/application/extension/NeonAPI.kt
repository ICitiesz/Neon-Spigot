package com.islandstudio.neon.stable.core.application.extension

import com.islandstudio.neon.stable.core.application.AppContext
import com.islandstudio.neon.stable.core.application.di.ModuleInjector
import com.islandstudio.neon.stable.core.application.server.ServerRunningMode
import org.koin.core.component.inject

object NeonAPI: ModuleInjector {
    private val appContext by inject<AppContext>()

    fun getServerRunningMode(): ServerRunningMode {
        return appContext.serverRunningMode
    }
}