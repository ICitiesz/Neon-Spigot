package com.islandstudio.neon;

import com.islandstudio.neon.Experimental.iCutter.ICutter;
import com.islandstudio.neon.Stable.New.Initialization.PluginConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;


public final class MainCore extends JavaPlugin {
    @Override
    public void onEnable() {
        try {
            PluginConstructor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        PluginConstructor.sendOutro();
    }
}
