package com.islandstudio.neon.shared.core.di

import org.koin.core.Koin
import org.koin.core.component.KoinComponent

interface IComponentInjector: KoinComponent {
    override fun getKoin(): Koin = AppDIManager.getKoinApp().koin
}