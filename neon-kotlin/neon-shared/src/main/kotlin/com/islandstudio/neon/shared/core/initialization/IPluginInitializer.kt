package com.islandstudio.neon.shared.core.initialization

interface IPluginInitializer {
    fun onLoad()

    fun onEnable()

    fun onDisable()
}