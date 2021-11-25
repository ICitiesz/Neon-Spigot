package com.islandstudio.neon.Experimental.iSmelter;

import org.bukkit.Material;

public enum Smeltable {
    SAND_GLASS(Material.getMaterial("SAND"), Material.getMaterial("GLASS")),
    RED_SAND_GLASS(Material.getMaterial("RED_SAND"), Material.getMaterial("GLASS")),
    STONE(Material.getMaterial("COBBLESTONE"), Material.getMaterial("STONE")),
    SMOOTH_SANDSTONE(Material.getMaterial("SANDSTONE"), Material.getMaterial("SMOOTH_SANDSTONE")),
    SMOOTH_RED_SANDSTONE(Material.getMaterial("RED_SANDSTONE"), Material.getMaterial("SMOOTH_RED_SANDSTONE")),
    SMOOTH_STONE(Material.getMaterial("STONE"), Material.getMaterial("SMOOTH_STONE")),
    SMOOTH_QUARTZ(Material.getMaterial("QUARTZ_BLOCK"), Material.getMaterial("SMOOTH_QUARTZ")),
    BRICK(Material.getMaterial("CLAY_BALL"), Material.getMaterial("BRICK")),
    NETHER_BRICK(Material.getMaterial("NETHERRACK"), Material.getMaterial("NETHER_BRICK")),
    CRACKED_NETHER_BRICKS(Material.getMaterial("NETHER_BRICKS"), Material.getMaterial("CRACKED_NETHER_BRICKS")),
    TERRACOTTA(Material.getMaterial("CLAY"), Material.getMaterial("TERRACOTTA")),
    CRACKED_STONE_BRICKS(Material.getMaterial("STONE_BRICKS"), Material.getMaterial("CRACKED_STONE_BRICKS")),

    WHITE_GLAZED_TERRACOTTA(Material.getMaterial("WHITE_TERRACOTTA"), Material.getMaterial("WHITE_GLAZED_TERRACOTTA")),
    ORANGE_GLAZED_TERRACOTTA(Material.getMaterial("ORANGE_TERRACOTTA"), Material.getMaterial("ORANGE_GLAZED_TERRACOTTA")),
    MAGENTA_GLAZED_TERRACOTTA(Material.getMaterial("MAGENTA_TERRACOTTA"), Material.getMaterial("MAGENTA_GLAZED_TERRACOTTA")),
    LIGHT_BLUE_GLAZED_TERRACOTTA(Material.getMaterial("LIGHT_BLUE_TERRACOTTA"), Material.getMaterial("LIGHT_BLUE_GLAZED_TERRACOTTA")),
    YELLOW_GLAZED_TERRACOTTA(Material.getMaterial("YELLOW_TERRACOTTA"), Material.getMaterial("YELLOW_GLAZED_TERRACOTTA")),
    LIME_GLAZED_TERRACOTTA(Material.getMaterial("LIME_TERRACOTTA"), Material.getMaterial("LIME_GLAZED_TERRACOTTA")),
    PINK_GLAZED_TERRACOTTA(Material.getMaterial("PINK_TERRACOTTA"), Material.getMaterial("PINK_GLAZED_TERRACOTTA")),
    GRAY_GLAZED_TERRACOTTA(Material.getMaterial("GRAY_TERRACOTTA"), Material.getMaterial("GRAY_GLAZED_TERRACOTTA")),
    LIGHT_GRAY_GLAZED_TERRACOTTA(Material.getMaterial("LIGHT_TERRACOTTA"), Material.getMaterial("LIGHT_GRAY_GLAZED_TERRACOTTA")),
    CYAN_GLAZED_TERRACOTTA(Material.getMaterial("CYAN_TERRACOTTA"), Material.getMaterial("CYAN_GLAZED_TERRACOTTA")),
    PURPLE_GLAZED_TERRACOTTA(Material.getMaterial("PURPLE_TERRACOTTA"), Material.getMaterial("PURPLE_GLAZED_TERRACOTTA")),
    BLUE_GLAZED_TERRACOTTA(Material.getMaterial("BLUE_TERRACOTTA"), Material.getMaterial("BLUE_GLAZED_TERRACOTTA")),
    BROWN_GLAZED_TERRACOTTA(Material.getMaterial("BROWN_TERRACOTTA"), Material.getMaterial("BROWN_GLAZED_TERRACOTTA")),
    GREEN_GLAZED_TERRACOTTA(Material.getMaterial("GREEN_TERRACOTTA"), Material.getMaterial("GREEN_GLAZED_TERRACOTTA")),
    RED_GLAZED_TERRACOTTA(Material.getMaterial("RED_TERRACOTTA"), Material.getMaterial("RED_GLAZED_TERRACOTTA")),
    BLACK_GLAZED_TERRACOTTA(Material.getMaterial("BLACK_TERRACOTTA"), Material.getMaterial("BLACK_GLAZED_TERRACOTTA")),

    GREEN_DYE(Material.getMaterial("CACTUS"), Material.getMaterial("GREEN_DYE")),

    OAK_WOOD_CHARCOAL(Material.getMaterial("OAK_WOOD"), Material.getMaterial("CHARCOAL")),
    SPRUCE_WOOD_CHARCOAL(Material.getMaterial("SPRUCE_WOOD"), Material.getMaterial("CHARCOAL")),
    BIRCH_WOOD_CHARCOAL(Material.getMaterial("BIRCH_WOOD"), Material.getMaterial("CHARCOAL")),
    JUNGLE_WOOD_CHARCOAL(Material.getMaterial("JUNGLE_WOOD"), Material.getMaterial("CHARCOAL")),
    ACACIA_WOOD_CHARCOAL(Material.getMaterial("ACACIA_WOOD"), Material.getMaterial("CHARCOAL")),
    DARK_OAK_WOOD_CHARCOAL(Material.getMaterial("DARK_OAK_WOOD"), Material.getMaterial("CHARCOAL")),

    OAK_LOG_CHARCOAL(Material.getMaterial("OAK_LOG"), Material.getMaterial("CHARCOAL")),
    SPRUCE_LOG_CHARCOAL(Material.getMaterial("SPRUCE_LOG"), Material.getMaterial("CHARCOAL")),
    BIRCH_LOG_CHARCOAL(Material.getMaterial("BIRCH_LOG"), Material.getMaterial("CHARCOAL")),
    JUNGLE_LOG_CHARCOAL(Material.getMaterial("JUNGLE_LOG"), Material.getMaterial("CHARCOAL")),
    ACACIA_LOG_CHARCOAL(Material.getMaterial("ACACIA_LOG"), Material.getMaterial("CHARCOAL")),
    DARK_OAK_LOG_CHARCOAL(Material.getMaterial("DARK_OAK_LOG"), Material.getMaterial("CHARCOAL")),

    STRIPPED_OAK_WOOD_CHARCOAL(Material.getMaterial("STRIPPED_OAK_WOOD"), Material.getMaterial("CHARCOAL")),
    STRIPPED_SPRUCE_WOOD_CHARCOAL(Material.getMaterial("STRIPPED_SPRUCE_WOOD"), Material.getMaterial("CHARCOAL")),
    STRIPPED_BIRCH_WOOD_CHARCOAL(Material.getMaterial("STRIPPED_BIRCH_WOOD"), Material.getMaterial("CHARCOAL")),
    STRIPPED_JUNGLE_WOOD_CHARCOAL(Material.getMaterial("STRIPPED_JUNGLE_WOOD"), Material.getMaterial("CHARCOAL")),
    STRIPPED_ACACIA_WOOD_CHARCOAL(Material.getMaterial("STRIPPED_ACACIA_WOOD"), Material.getMaterial("CHARCOAL")),
    STRIPPED_DARK_OAK_WOOD_CHARCOAL(Material.getMaterial("STRIPPED_DARK_OAK_WOOD"), Material.getMaterial("CHARCOAL")),

    STRIPPED_OAK_LOG_CHARCOAL(Material.getMaterial("STRIPPED_OAK_LOG"), Material.getMaterial("CHARCOAL")),
    STRIPPED_SPRUCE_LOG_CHARCOAL(Material.getMaterial("STRIPPED_SPRUCE_LOG"), Material.getMaterial("CHARCOAL")),
    STRIPPED_BIRCH_LOG_CHARCOAL(Material.getMaterial("STRIPPED_BIRCH_LOG"), Material.getMaterial("CHARCOAL")),
    STRIPPED_JUNGLE_LOG_CHARCOAL(Material.getMaterial("STRIPPED_JUNGLE_LOG"), Material.getMaterial("CHARCOAL")),
    STRIPPED_ACACIA_LOG_CHARCOAL(Material.getMaterial("STRIPPED_ACACIA_LOG"), Material.getMaterial("CHARCOAL")),
    STRIPPED_DARK_OAK_LOG_CHARCOAL(Material.getMaterial("STRIPPED_DARK_OAK_LOG"), Material.getMaterial("CHARCOAL")),

    POPPED_CHORUS_FRUIT(Material.getMaterial("CHORUS_FRUIT"), Material.getMaterial("POPPED_CHORUS_FRUIT")),
    SPONGE(Material.getMaterial("WET_SPONGE"), Material.getMaterial("SPONGE")),
    LIME_DYE(Material.getMaterial("SEA_PICKLE"), Material.getMaterial("LIME_DYE"));

    private final Material inputMaterial;
    private final Material outputMaterial;

    Smeltable(Material inputMaterial, Material outputMaterial) {
        this.inputMaterial = inputMaterial;
        this.outputMaterial = outputMaterial;
    }

    public Material getInput() {
        return inputMaterial;
    }

    public Material getOutput() {
        return outputMaterial;
    }
}
