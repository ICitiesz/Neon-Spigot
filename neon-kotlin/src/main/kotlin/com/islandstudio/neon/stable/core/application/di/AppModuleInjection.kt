package com.islandstudio.neon.stable.core.application.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.koinApplication

object AppModuleInjection {
    private val koinApplication = koinApplication {}

    fun run() {
        loadAppModule()
        startKoin(koinApplication)
    }

    fun stopModuleInjection() {
        koinApplication.close()
    }

    fun getKoinApplication(): KoinApplication = koinApplication

    private fun loadAppModule() {
//        koinApplication.modules(
//            GeneralModule().module,
//            DatabaseModule().module,
//            com.islandstudio.neon.shared.di.module.GeneralModule().module
//        )
    }
}