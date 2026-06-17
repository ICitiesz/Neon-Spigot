package com.islandstudio.neon.shared.rework.core.initialization

interface IPluginInitializer {
    fun onLoad()

    fun onEnable()

    fun onDisable()
}