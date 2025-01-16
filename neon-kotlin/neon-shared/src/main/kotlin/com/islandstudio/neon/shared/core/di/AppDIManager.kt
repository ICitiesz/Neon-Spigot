package com.islandstudio.neon.shared.core.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.koinApplication

object AppDIManager {
    private val koinApp = koinApplication {}

    //TODO: Need add loading message
    fun run() {
        startKoin(koinApp)
    }

    fun loadDIModule(vararg diModules: Module): AppDIManager {
        koinApp.modules(*diModules)
        return this
    }

    fun getKoinApp(): KoinApplication = koinApp
}