package com.islandstudio.neon.core.datakey

import com.islandstudio.neon.shared.core.di.IComponentProvider
import com.islandstudio.neon.shared.core.di.getComponent
import com.islandstudio.neon.shared.core.initialization.IPluginContext
import org.bukkit.NamespacedKey

abstract class AbstractDataKey(keyName: String): IComponentProvider {
    private val pluginContext = getComponent<IPluginContext>()
    private val dataKeyManager = getComponent<DataKeyManager>()

    val dataKey = NamespacedKey(pluginContext.mainPluginInstance, dataKeyManager.fromProperty(keyName))
}