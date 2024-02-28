package com.islandstudio.neon

import com.islandstudio.neon.stable.core.init.NConstructor
import org.bukkit.plugin.java.JavaPlugin

class Neon : JavaPlugin() {
    override fun onLoad() {
        NConstructor.preConstructPlugin()
    }

    override fun onEnable() {
        NConstructor.postConstructPlugin()
    }

    override fun onDisable() {
        NConstructor.displayClosingTitle()
    }
}