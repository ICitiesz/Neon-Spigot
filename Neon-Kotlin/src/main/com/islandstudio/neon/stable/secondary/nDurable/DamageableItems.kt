package com.islandstudio.neon.stable.secondary.nDurable

object DamageableItems {
    enum class Items(val itemName: String) {
        /* Weapons */
        SWORD("sword"),
        TRIDENT("trident"),
        BOW("bow"),
        CROSSBOW("crossbow"),

        /* Tools */
        PICKAXE("_pickaxe"),
        AXE("_axe"),
        SHOVEL("_shovel"),
        HOE("_hoe"),
        SHEARS("shears"),
        FISHING_ROD("fishing_rod"),
        FLINT_AND_STEEL("flint_and_steel")
    }

    enum class Blocks(val blockName: String) {
        LOG("_log"),
        WOOD("_wood"),
        STEM("_stem"),
        EXPOSED("exposed"),
        WEATHERED("weathered"),
        OXIDIZED("oxidized"),
        WAXED("waxed_"),
        PUMPKIN("pumpkin"),
        COMMAND_BLOCK("command_block"),
        FENCE("_fence"),
        ROOTED_DIRT("rooted_dirt"),
        GRASS_BLOCK("grass_block"),
        DIRT_BLOCK("dirt"),
        DIRT_PATH("dirt_path"),
        COARSE_DIRT("coarse_dirt")
    }
}