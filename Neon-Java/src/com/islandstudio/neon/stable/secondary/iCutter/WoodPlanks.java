package com.islandstudio.neon.stable.secondary.iCutter;

import org.bukkit.Material;

public enum WoodPlanks {
    OAK(Material.getMaterial("OAK_PLANKS")),
    SPRUCE(Material.getMaterial("SPRUCE_PLANKS")),
    BIRCH(Material.getMaterial("BIRCH_PLANKS")),
    JUNGLE(Material.getMaterial("JUNGLE_PLANKS")),
    ACACIA(Material.getMaterial("ACACIA_PLANKS")),
    DARK_OAK(Material.getMaterial("DARK_OAK_PLANKS")),
    WARPED(Material.getMaterial("WARPED_PLANKS")),
    CRIMSON(Material.getMaterial("CRIMSON_PLANKS"));

    private final Material woodType;

    WoodPlanks(Material woodType) {
        this.woodType = woodType;
    }

    public Material getWoodType() {
        return woodType;
    }
}
