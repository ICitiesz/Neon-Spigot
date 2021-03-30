package com.islandstudio.neon;

import com.islandstudio.neon.Stable.New.Initialization.PluginConstructor;
import com.islandstudio.neon.Stable.New.PluginFeatures.RankSystem.RankTags;
import org.bukkit.plugin.java.JavaPlugin;

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
