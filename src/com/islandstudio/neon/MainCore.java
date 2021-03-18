package com.islandstudio.neon;

import com.islandstudio.neon.Stable.New.Initialization.PluginConstructor;
import com.islandstudio.neon.Stable.New.PluginFeatures.RankSystem.RankTags;
import org.bukkit.plugin.java.JavaPlugin;

public final class MainCore extends JavaPlugin {
    @Override
    public void onEnable() {
        try {
            PluginConstructor.start();
            RankTags.test();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {
        //VersionHandler.sendOutro();
    }

    public final void loadConfig() {
        this.getConfig().options().copyDefaults(true);
        this.getConfig().options().copyHeader(true);
        this.saveConfig();
    }
}
