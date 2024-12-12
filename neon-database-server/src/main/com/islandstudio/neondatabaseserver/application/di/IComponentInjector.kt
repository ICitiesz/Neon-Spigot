package com.islandstudio.neondatabaseserver.application.di

import org.koin.core.Koin
import org.koin.core.component.KoinComponent

interface IComponentInjector: KoinComponent {
    override fun getKoin(): Koin = AppModuleInjection.getKoinApplication().koin
}