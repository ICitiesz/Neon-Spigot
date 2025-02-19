package com.islandstudio.neon.api.service.system.impl

import com.islandstudio.neon.api.repository.system.IDatabaseInfoRepository
import com.islandstudio.neon.api.service.system.IDatabaseInfoService
import com.islandstudio.neon.shared.core.di.IComponentInjector
import org.koin.core.annotation.Single
import org.koin.core.component.inject

@Single
class DatabaseInfoService: IDatabaseInfoService, IComponentInjector {
    private val databaseInfoRepository by inject<IDatabaseInfoRepository>()
    override fun isConnected(): Boolean {
        return databaseInfoRepository.existConnected()
    }
}