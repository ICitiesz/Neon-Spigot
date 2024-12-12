package com.islandstudio.neon.stable.core.application.extension

import com.islandstudio.neon.stable.core.application.AppContext
import com.islandstudio.neon.stable.core.application.di.IComponentInjector
import com.islandstudio.neon.stable.core.application.server.ServerRunningMode
import com.islandstudio.neon.stable.core.database.DatabaseInterface
import org.koin.core.component.inject

object NeonAPI: IComponentInjector {
    private val appContext by inject<AppContext>()
    private val dbInterface by inject<DatabaseInterface>()

    fun getServerRunningMode(): ServerRunningMode {
        return appContext.serverRunningMode
    }
}