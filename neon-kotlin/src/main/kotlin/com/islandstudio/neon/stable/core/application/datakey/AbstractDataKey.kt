package com.islandstudio.neon.stable.core.application.datakey

import com.islandstudio.neon.Neon
import com.islandstudio.neon.shared.core.di.IComponentInjector
import org.bukkit.NamespacedKey
import org.koin.core.component.inject

abstract class AbstractDataKey(keyName: String): IComponentInjector {
    private val neon by inject<Neon>()
    private val dataKeyManager by inject<DataKeyManager>()

    val dataKey = NamespacedKey(neon, dataKeyManager.fromProperty(keyName))
}