package com.islandstudio.neon;

import com.islandstudio.neon.stable.primary.iConstructor.IConstructor;
import org.bukkit.plugin.java.JavaPlugin;


public final class Neon extends JavaPlugin {
    @Override
    public void onLoad() {
        IConstructor.buildPrimary();
    }

    @Override
    public void onEnable() {
       IConstructor.buildSecondary();
    }

    @Override
    public void onDisable() {
        IConstructor.sendOutro();
    }
}
