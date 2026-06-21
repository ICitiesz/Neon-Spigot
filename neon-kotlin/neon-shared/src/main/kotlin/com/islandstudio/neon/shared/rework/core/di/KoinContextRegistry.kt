package com.islandstudio.neon.shared.rework.core.di

import org.koin.core.Koin
import java.util.concurrent.ConcurrentHashMap

object KoinContextRegistry {
    private val registredContext = ConcurrentHashMap<ClassLoader, Koin>()

    fun registerContext(classLoader: ClassLoader, koin: Koin) {
        registredContext[classLoader] = koin
    }

    fun getContext(classLoader: ClassLoader): Koin {
        return registredContext[classLoader]
            ?: registredContext[classLoader.parent]
            ?: throw IllegalStateException("Dependency injection context not found!")
    }

    fun clear() {
        registredContext.clear()
    }
}