package com.islandstudio.neon.shared.rework.core.di

import org.koin.core.Koin
import org.koin.core.parameter.ParametersHolder

interface IComponentProvider {
    fun getBoostrapScopedKoin(): Koin {
        return BootstrapDIManager.getKoin()
    }

    fun getPluginScopedKoin(): Koin {
        return PluginDIManager.getKoin()
    }
}

inline fun <reified T : Any> IComponentProvider.getBootstrapScopedComponent(vararg parameters: Any? = emptyArray()): T {
    return getBoostrapScopedKoin().get<T>(parameters = { ParametersHolder(parameters.toMutableList()) })
}

inline fun <reified T : Any> IComponentProvider.injectBootstrapScopedComponent(): Lazy<T> {
    return getBoostrapScopedKoin().inject<T>()
}

inline fun <reified T : Any> IComponentProvider.getPluginScopedComponent(vararg parameters: Any? = emptyArray()): T {
    return getPluginScopedKoin().get<T>(parameters = { ParametersHolder(parameters.toMutableList()) })
}

inline fun <reified T : Any> IComponentProvider.injectPluginScopedComponent(): Lazy<T> {
    return getPluginScopedKoin().inject<T>()
}