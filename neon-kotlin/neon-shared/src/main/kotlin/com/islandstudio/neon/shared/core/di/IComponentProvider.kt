package com.islandstudio.neon.shared.core.di

import org.koin.core.Koin

interface IComponentProvider {
    fun getKoin(): Koin {
        return try {
            GlobalDIManager.getKoin()
        } catch (e: Exception) {
            PluginDIManager.getKoin()
        }
    }
}

inline fun <reified T : Any> IComponentProvider.getComponent(): T {
    return getKoin().get<T>()
}