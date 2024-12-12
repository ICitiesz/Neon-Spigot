package com.islandstudio.neon.stable.core.application.di

import org.koin.core.Koin
import org.koin.core.component.KoinComponent

interface IComponentInjector: KoinComponent {
    override fun getKoin(): Koin = AppModuleInjection.getKoinApplication().koin
}