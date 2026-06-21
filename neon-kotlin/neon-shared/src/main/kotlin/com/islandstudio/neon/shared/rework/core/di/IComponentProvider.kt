package com.islandstudio.neon.shared.rework.core.di

import com.islandstudio.neon.shared.rework.core.di.IComponentProvider.Companion.getKoinContextByClassLoader
import org.koin.core.Koin
import org.koin.core.parameter.ParametersHolder

interface IComponentProvider {
    companion object {
        internal fun getKoinContextByClassLoader(): Koin {
            return KoinContextRegistry.getContext(this.javaClass.classLoader)
        }
    }
}

fun getKoinContext(): Koin {
    return getKoinContextByClassLoader()
}

inline fun <reified T : Any> IComponentProvider.injectComponent(): Lazy<T> {
    return getKoinContext().inject<T>()
}

inline fun <reified T : Any> IComponentProvider.getComponent(vararg parameters: Any? = emptyArray()): T {
    return getKoinContext().get<T>(parameters = { ParametersHolder(parameters.toMutableList()) })
}