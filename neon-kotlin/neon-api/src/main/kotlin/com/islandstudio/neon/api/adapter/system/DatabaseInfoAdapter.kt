package com.islandstudio.neon.api.adapter.system

import com.islandstudio.neon.api.service.system.IDatabaseInfoService
import com.islandstudio.neon.shared.core.di.IComponentInjector
import org.koin.core.annotation.Single
import org.koin.core.component.inject

@Single
class DatabaseInfoAdapter: IComponentInjector {
    private val databaseInfoService by inject<IDatabaseInfoService>()

    fun performHealthCheck(): Boolean {
        return databaseInfoService.isConnected()
    }
}