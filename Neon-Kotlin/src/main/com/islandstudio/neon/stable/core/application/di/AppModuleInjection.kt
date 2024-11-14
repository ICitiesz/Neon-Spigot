package com.islandstudio.neon.stable.core.application.di

import com.islandstudio.neon.stable.core.application.di.module.DatabaseModule
import com.islandstudio.neon.stable.core.application.di.module.GeneralModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.koinApplication
import org.koin.ksp.generated.module

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
        koinApplication.modules(
            GeneralModule().module,
            DatabaseModule().module
        )
    }
}