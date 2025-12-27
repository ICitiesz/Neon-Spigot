package com.islandstudio.neon.shared.core.di

import org.koin.core.Koin
import org.koin.core.parameter.ParametersHolder

interface IComponentProvider {
    fun getKoin(): Koin {
        return try {
            GlobalDIManager.getKoin()
        } catch (e: Exception) {
            PluginDIManager.getKoin()
        }
    }
}

inline fun <reified T : Any> IComponentProvider.getComponent(vararg parameters: Any? = emptyArray()): T {
    return getKoin().get<T>(parameters = { ParametersHolder(parameters.toMutableList()) })
}

inline fun <reified T : Any> IComponentProvider.injectComponent(): Lazy<T> {
    return getKoin().inject<T>()
}